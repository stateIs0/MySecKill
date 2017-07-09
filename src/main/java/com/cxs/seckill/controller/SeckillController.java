package com.cxs.seckill.controller;

import com.cxs.seckill.dto.Exposer;
import com.cxs.seckill.dto.SeckillExecution;
import com.cxs.seckill.dto.SeckillResult;
import com.cxs.seckill.entity.Seckill;
import com.cxs.seckill.enums.SeckillStatEnum;
import com.cxs.seckill.exception.RepeatKillException;
import com.cxs.seckill.exception.SeckillCloseException;
import com.cxs.seckill.service.SeckillService;
import java.awt.PageAttributes.MediaType;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by cxsta on 2017/7/9.
 */
@Controller
@RequestMapping("/seckill")
public class SeckillController {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private SeckillService seckillService;

  // model用来渲染jsp的数据
  @RequestMapping(value = "/list", method = RequestMethod.GET,
      produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
  public String list(Model model) {
    // 获取列表页
    List<Seckill> list = seckillService.getSeckillList();
    model.addAttribute("list", list);
    //list.jsp + model = ModelAndView
    return "list";
  }

  @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET,
      produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
  // @PathVariable 可以不写
  public String deatil(Model model, @PathVariable("seckillId") Long seckillId) {
    if (seckillId == null) {
      return "redirect:/seckill/list";
    }
    if (seckillService.getById(seckillId) == null) {
      return "forward:/seckill/list";
    }
    model.addAttribute("seckill", seckillService.getById(seckillId));
    return "detail";
  }

  @RequestMapping(value = "/{seckillId}/exposer", method = RequestMethod.POST,
      produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)

  // ajax 返回的是jso
  @ResponseBody
  public SeckillResult<Exposer> exposer(Long seckillId) {
    SeckillResult<Exposer> result = null;
    try {
      Exposer exposer = seckillService.exportSeckillUrl(seckillId);
      result = new SeckillResult<Exposer>(true, exposer);
    } catch (Exception exp) {
      logger.error(exp.getLocalizedMessage(), exp);
      result = new SeckillResult<Exposer>(false, exp.getMessage());
    }
    return result;
  }

  @RequestMapping(value = "/{seckillId}/{md5}/execution",
      produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)

  public SeckillResult<SeckillExecution> executorSeckill(@PathVariable("seckillId") Long seckillId,
      @PathVariable("md5") String md5,@CookieValue(value = "killPhone",required = false) Long userPhone) {
    // spring mvc valid 是一种参数验证，可节省很多验证步骤
    if (userPhone == null) {
      return new SeckillResult<SeckillExecution>(false, "未注册");
    }
    try {
      SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId,userPhone,md5);
      return new SeckillResult<SeckillExecution>(true, seckillExecution);
    } catch(RepeatKillException e){
      SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL);
      return new SeckillResult<SeckillExecution>(false, seckillExecution);
    }catch (SeckillCloseException e){
      SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStatEnum.END);
      return new SeckillResult<SeckillExecution>(false, seckillExecution);
    }catch (Exception e){
      logger.error(e.getMessage(), e);
      SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
      return new SeckillResult<SeckillExecution>(false, seckillExecution);
    }
  }

  @RequestMapping(value = "/time/now",
      produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)

  public SeckillResult<Long> time(){
    return new SeckillResult<Long>(true, new Date().getTime());
  }
}
