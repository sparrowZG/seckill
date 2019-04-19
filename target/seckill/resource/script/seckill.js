//存放主要的交互逻辑js代码
//js 的模块化
var seckill = {
    //秒杀相关的URL
    URL: {
        now: function () {
            return '/seckill/time/now';
        },
        exposer: function (seckillId) {
            return '/seckill/' + seckillId + '/exposer';
        },
        execution: function (seckillId, md5) {
            return '/seckill/' + seckillId + '/' + md5 + '/execution';
        }
    },
    handleSeckillkill: function (seckillId, node) {
        node.hide().html('<button class="btn-primary btn-lg" id="killBtn">开始秒杀</button>');
        $.post(seckill.URL.exposer(seckillId), {}, function (result) {
            //在回调函数中执行交互流程
            if (result && result['success']) {
                var exposer = result['data'];
                if (exposer['exposed']) {
                    //开启秒杀
                    //获取秒杀地址
                    var md5 = exposer['md5'];
                    var killUrl = seckill.execution(seckillId, md5);
                    console.debug("killUrl:" + killUrl);
                    //绑定一次请求
                    $('#killBtn').one('click', function () {
                        //执行秒杀请求
                        $(this).addClass('disabled');
                        //2.发送秒杀请求
                        $.post(killUrl, {}, function (result) {
                            if (result && result['success']) {
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateInfo'];
                                //显示秒杀结果
                                node.html('<span class="label label-success">' + stateInfo + '</span>');
                            }
                        });
                    });
                    node.show();
                } else {
                    //未开启秒杀
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];
                    //重新计时
                    seckill.countdown(seckillId, now, start, end);
                }
            } else {
                console.log('result' + result);
            }
        });
    },
    //验证手机号
    validatePhone: function (phone) {
        if (phone && phone.length == 11 && !isNaN(phone)) {
            return true;
        } else {
            return false;
        }
    },
    countdown: function (seckillId, nowTime, startTime, endTime) {
        var seckillBox = $('#seckill-box');
        //时间判断
        if (nowTime > endTime) {//
            seckillBox.html('秒杀结束');
        } else if (nowTime < endTime) {
            //秒杀未开始,计时时间绑定
            var killTime = new Date(startTime + 1000);

            seckillBox.countdown(killTime, function (event) {
                var format = event.strftime('秒杀倒计时: %D天 %H时 %M分 %S秒');
                //事件完成后,回调事件
                seckillBox.html(format);
            }).on('finish.countdown', function () {
                //获取秒杀地址,控制实现逻辑,执行秒杀
                seckill.handleSeckillkill(seckillId, seckillBox);
            })
        } else {
            //秒杀开始
            seckill.handleSeckillkill(seckillId, seckillBox);
        }

    },
    //详情页秒杀逻辑
    detail: {
        //详情页初始化
        init: function (params) {
            //手机验证和登录,计时交互
            //规划我们的交互过程
            //在cookie中查找手机号
            var killPhone = $.cookie('killPhone');

            //验证手机号
            if (!seckill.validatePhone(killPhone)) {

                var killPhoneModal = $('#killPhoneModal');
                killPhoneModal.modal({
                    show: true,//显示弹出层
                    backdrop: 'static',//禁止位置关闭
                    keyboard: false//关闭键盘事件
                });
                $('#killPhoneBtn').click(function () {
                    //
                    var inputPhone = $('#killPhoneKey').val();
                    console.debug("inputPhone" + inputPhone);
                    if (seckill.validatePhone(inputPhone)) {

                        //写入电话,写入cookie
                        $.cookie('killPhone', inputPhone, {expires: 7, path: '/seckill'});
                        //刷新页面
                        window.location.reload();
                    } else {
                        $('#killPhoneMessage').hide().html('<lable class ="lable-danger">手机号错误!</lable>').show(300)
                    }
                });
            }
            //已经登录
            //计时交互
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            console.debug(startTime);
            $.get(seckill.URL.now(), {}, function (result) {
            console.debug(result);
                if (result && result['success']) {
                    var nowTime = result['data'];
                    //时间判断,即时交互
                    seckill.countdown(seckillId, nowTime, startTime, endTime);
                } else {
                    console.log('result:' + result);
                }
            });
        }
    }

}