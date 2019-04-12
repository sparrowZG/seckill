package com.sparrow.zg.web;

import com.sparrow.zg.dto.Exposer;
import com.sparrow.zg.dto.SeckillExecution;
import com.sparrow.zg.dto.SeckillResult;
import com.sparrow.zg.entity.Seckill;
import com.sparrow.zg.entity.SuccessKilled;
import com.sparrow.zg.enums.SeckillStateEnum;
import com.sparrow.zg.exception.RepeatKillException;
import com.sparrow.zg.exception.SeckillCloseException;
import com.sparrow.zg.exception.SeckillException;
import com.sparrow.zg.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@Controller
@RequestMapping("/seckill")   //url:/模块/资源/{id}/细分/seckill/list
public class SeckillController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SeckillService seckillService;
	@RequestMapping(name="/list",method = RequestMethod.GET)
	public String list(Model model){
		List<Seckill> list = seckillService.getSeckillList();
		model.addAttribute("list",list);
		return "list";
	}
	@RequestMapping(value = "/{seckillId}/detail",method = RequestMethod.GET)
	public String detail(@PathVariable("seckillId") Long seckillId, Model model){
		if(seckillId == null){
			return "redirect:/seckill/list";
		}
		Seckill seckill = seckillService.getById(seckillId);
		model.addAttribute("seckill",seckill);
		return "detail";
	}
	//ajax json
	@RequestMapping(value = "/{seckillId}/exposer",
		method = RequestMethod.POST,
		produces = {"application/json;charset=UTF-8"}
	)
	@ResponseBody
	public SeckillResult<Exposer> exposer(Long seckillId){
		SeckillResult<Exposer> result;
		try {
			Exposer exposer = seckillService.exportSeckillUrl(seckillId);
			result = new SeckillResult<Exposer>(true,exposer);
		}catch (Exception e){

		}
		return null;
	}
	@RequestMapping(value = "/{seckillId}/{md5}/execution",
	method = RequestMethod.POST,
	produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public SeckillResult<SeckillExecution> execute(
		@PathVariable("seckillId")Long seckillId,
		@PathVariable("md5")String md5,
		@CookieValue(value = "killPhone",required = false)Long phone){
		if(phone==null){
			return new SeckillResult<SeckillExecution>(false,"未注册");
		}
		SeckillResult<SeckillExecution> result;
		try{
			SeckillExecution execution =seckillService.executeSeckill(seckillId,phone,md5);
			return new SeckillResult<SeckillExecution>(true,execution);
		}catch (RepeatKillException e1){
			SeckillExecution execution = new SeckillExecution(seckillId,SeckillStateEnum.REPEAT_KILL);
			return new SeckillResult<SeckillExecution>(false,execution);
		}catch (SeckillCloseException e2){
			SeckillExecution execution = new SeckillExecution(seckillId,SeckillStateEnum.END);
			return new SeckillResult<SeckillExecution>(false,execution);
		}catch (Exception e){
			logger.error(e.getMessage(),e);
			SeckillExecution execution = new SeckillExecution(seckillId,SeckillStateEnum.INNER_ERROR);
			return new SeckillResult<SeckillExecution>(false,execution);
		}
	}
	@RequestMapping(value = "/time/now",method = RequestMethod.GET)
	public SeckillResult<Long> time(){
		Date now = new Date();
		return new SeckillResult(true,now.getTime());
	}


}
