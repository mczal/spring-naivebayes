package com.mczal.nb.controller;

import com.mczal.nb.dto.MapReduceTestingDtoRequest;
import com.mczal.nb.dto.MapReduceTrainingDtoRequest;
import com.mczal.nb.service.hdfs.HdfsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.io.*;

/**
 * Created by mczal on 07/06/17.
 */
@Controller
@RequestMapping(value = MapReduceController.ABSOLUTE_PATH)
public class MapReduceController {

  public static final String ABSOLUTE_PATH = "/admin/mapreduce-control";

  private static final String LAYOUTS_ADMIN = "layouts/admin";

  private static final int TEST = 1;

  private static final int TRAIN = 0;

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Value("${hadoop.run.testing}")
  private String _RUN_TESTING;

  @Value("${hadoop.run.training}")
  private String _RUN_TRAINING;

  @Autowired
  private HdfsService hdfsService;

  private void createShellFile(int type, String model) throws Exception {
    if (type == TRAIN) {
      String mfsWorkspace = System.getenv("MFS_WORKSPACE");

      String path = "";
      if (mfsWorkspace == null || mfsWorkspace.equals("")) {
        path =
            "/Users/mczal/Projects/MapReduce-workspace/spring-naivebayes/src/main/resources/run-training.sh";
      } else {
        path = mfsWorkspace + "/run-training.sh";
      }

      File file = new File(path);
      if (file.exists()) {
        file.delete();
        file = new File(path);
      }
      if (file.setExecutable(true)) {
        logger.error("Error set Executable");
      }
      if (file.setReadable(true)) {
        logger.error("Error set Readable");
      }
      ;
      if (file.setWritable(true)) {
        logger.error("Error set Writable");
      }
      ;
      BufferedWriter bw = new BufferedWriter(new FileWriter(file));
      bw.write("#!/bin/sh");
      bw.newLine();
      bw.write("echo \"RUNNING MAPREDUCE JOB - Training\"");
      bw.newLine();
      //      bw.write("/Users/Shared/hadoop/bin/hadoop jar "
      //          + "/Users/mczal/Projects/MapReduce-workspace/mapreduce-naivebayes-training/target/mapreduce-1.0-SNAPSHOT.jar "
      //          + "mczal.bayes.App " + model + System.lineSeparator());
      bw.write(_RUN_TRAINING + " " + model + System.lineSeparator());

      bw.newLine();
      bw.write("echo \"DONE\"");
      bw.flush();
      bw.close();
      Runtime.getRuntime().exec("chmod 775 " + path);
    } else if (type == TEST) {
      String mfsWorkspace = System.getenv("MFS_WORKSPACE");

      String path = "";
      if (mfsWorkspace == null || mfsWorkspace.equals("")) {
        path =
            "/Users/mczal/Projects/MapReduce-workspace/spring-naivebayes/src/main/resources/run-testing.sh";
      } else {
        path = mfsWorkspace + "/run-testing.sh";
      }
      File file = new File(path);
      if (file.exists()) {
        file.delete();
        file = new File(path);
      }
      if (file.setExecutable(true)) {
        logger.error("Error set Executable");
      }
      if (file.setReadable(true)) {
        logger.error("Error set Readable");
      }
      ;
      if (file.setWritable(true)) {
        logger.error("Error set Writable");
      }
      ;
      BufferedWriter bw = new BufferedWriter(new FileWriter(file));
      bw.write("#!/bin/sh");
      bw.newLine();
      bw.write("echo \"RUNNING MAPREDUCE JOB - Testing\"");
      bw.newLine();
      //      bw.write("/Users/Shared/hadoop/bin/hadoop jar "
      //          + "/Users/mczal/Projects/MapReduce-workspace/mapreduce-naivebayes-testing/target/testing-1.0-SNAPSHOT.jar "
      //          + "App " + model + System.lineSeparator());
      bw.write(_RUN_TESTING + " " + model + System.lineSeparator());
      bw.newLine();
      bw.write("echo \"DONE\"");
      bw.flush();
      bw.close();
      Runtime.getRuntime().exec("chmod 775 " + path);
    } else {
      throw new Exception("Undefined Type");
    }
  }

  private void executeCommand(String command) {
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
  }

  @RequestMapping(value = {"", "/"},
      method = RequestMethod.GET)
  public String index(Model model) throws Exception {
    model.addAttribute("view", "mapreduce/index");
    model.addAttribute("availableDirs", hdfsService.listInputDirOnPath());
    model.addAttribute("testing", new MapReduceTestingDtoRequest());
    model.addAttribute("training", new MapReduceTrainingDtoRequest());
    return LAYOUTS_ADMIN;
  }

  @RequestMapping(value = "/testing",
      method = RequestMethod.POST)
  public String runTesting(@Valid MapReduceTestingDtoRequest testing, BindingResult bindingResult)
      throws Exception {
    if (bindingResult.hasErrors()) {
      throw new Exception(bindingResult.getFieldError() + "");
    }
    this.createShellFile(TEST, testing.getModel());
    String mfsWorkspace = System.getenv("MFS_WORKSPACE");

    String command = "";
    if (mfsWorkspace == null || mfsWorkspace.equals("")) {
      command =
          "/Users/mczal/Projects/MapReduce-workspace/spring-naivebayes/src/main/resources/run-testing.sh";
    } else {
      command = mfsWorkspace + "/run-testing.sh";
    }

    //    String command = _RUN_TRAINING;
    //    String command = "ping google.com";
    this.executeCommand(command);

    logger.info("Your cmd: " + command);



    return "redirect:" + ABSOLUTE_PATH;
  }

  @RequestMapping(value = "/training",
      method = RequestMethod.POST)
  public String runTraining(@Valid MapReduceTrainingDtoRequest training,
      BindingResult bindingResult) throws Exception {
    if (bindingResult.hasErrors()) {
      throw new Exception(bindingResult.getFieldError() + "");
    }

    this.createShellFile(TRAIN, training.getModel());

    String mfsWorkspace = System.getenv("MFS_WORKSPACE");
    String command = "";
    if (mfsWorkspace == null || mfsWorkspace.equals((""))) {
      command =
          "/Users/mczal/Projects/MapReduce-workspace/spring-naivebayes/src/main/resources/run-training.sh";
    } else {
      command = mfsWorkspace + "/run-training.sh";
    }
    //    String command = _RUN_TRAINING;
    //    String command = "ping google.com";

    logger.info("Your cmd: " + command);

    this.executeCommand(command);

    return "redirect:" + ABSOLUTE_PATH;
  }
}
