package com.cxs.seckill.controller;

import com.cxs.seckill.dto.Exposer;
import com.cxs.seckill.dto.SeckillResult;
import com.cxs.seckill.entity.Seckill;
import com.cxs.seckill.service.SeckillService;
import java.awt.PageAttributes.MediaType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
  @RequestMapping(value = "/list", method = RequestMethod.GET)
  public String list(Model model) {
    // 获取列表页
    List<Seckill> list = seckillService.getSeckillList();
    model.addAttribute("list", list);
    //list.jsp + model = ModelAndView
    return "list";
  }

  @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
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

  @RequestMapping(value = "/{seckillId}/exposer", method = RequestMethod.POST)
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

  @RequestMapping("/{seckillId}/{md5}/execution")
  public SeckillResult<Exposer> executorSeckill() {
    return null;
  }
}
