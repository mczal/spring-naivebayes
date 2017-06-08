package com.mczal.nb.controller;

import com.mczal.nb.dto.MapReduceTestingDtoRequest;
import com.mczal.nb.dto.MapReduceTrainingDtoRequest;
import com.mczal.nb.service.hdfs.HdfsService;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by mczal on 07/06/17.
 */
@Controller
@RequestMapping(value = MapReduceController.ABSOLUTE_PATH)
public class MapReduceController {

  public static final String ABSOLUTE_PATH = "/admin/mapreduce-control";
  private static final String LAYOUTS_ADMIN = "layouts/admin";
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private HdfsService hdfsService;

  @Value("${hadoop.run.training}")
  private String _RUN_TRAINING;

  @Value("${hadoop.run.testing}")
  private String _RUN_TESTING;

  @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
  public String index(Model model) throws Exception {
    model.addAttribute("view", "mapreduce/index");
    model.addAttribute("availableDirs", hdfsService.listInputDirOnPath());
    model.addAttribute("testing", new MapReduceTestingDtoRequest());
    model.addAttribute("training", new MapReduceTrainingDtoRequest());
    return LAYOUTS_ADMIN;
  }

  @RequestMapping(value = "/testing", method = RequestMethod.POST)
  public String runTesting(@Valid MapReduceTestingDtoRequest testing, BindingResult bindingResult)
      throws Exception {
    if (bindingResult.hasErrors()) {
      throw new Exception(bindingResult.getFieldError() + "");
    }
    throw new Exception("NOT IMPLEMENTED");
  }

  @RequestMapping(value = "/training", method = RequestMethod.POST)
  public String runTraining(@Valid MapReduceTrainingDtoRequest training,
      BindingResult bindingResult) throws Exception {
    if (bindingResult.hasErrors()) {
      throw new Exception(bindingResult.getFieldError() + "");
    }
    String command = _RUN_TRAINING + training.getModel();
//    String command = "ping google.com";

    logger.info("Your cmd: " + command);

    try {

      Runtime rt = Runtime.getRuntime();
      Process proc = rt.exec(command);
      proc.waitFor();

      StringBuffer output = new StringBuffer();

      BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
      String line = "";
      while ((line = reader.readLine()) != null) {
        output.append(line + "\n");
      }
      System.out.println("### " + output);

    } catch (Throwable t) {

      t.printStackTrace();

    }

    return "redirect:" + ABSOLUTE_PATH;
  }
}
