package com.mczal.nb.controller;

import com.mczal.nb.dto.InputSetDtoRequest;
import com.mczal.nb.service.hdfs.HdfsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by mczal on 08/03/17.
 */
@Controller
@RequestMapping(InputSetController.ABSOLUTE_PATH)
public class InputSetController {

  public static final String ABSOLUTE_PATH = "/admin/input-set";

  private static final String LAYOUTS_ADMIN = "layouts/admin";

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private HdfsService hdfsService;

  @Value("${hdfs.input.regex}")
  private String regex;

  @RequestMapping({"", "/"})
  public String index(Model model) throws Exception {
    return "redirect:" + ABSOLUTE_PATH + "/both";
  }

  @RequestMapping("/both")
  public String indexBoth(Model model) throws Exception {
    //    return "redirect:/admin/home";
    model.addAttribute("view", "input-set-both");
    model.addAttribute("inputSet", new InputSetDtoRequest());
    //    logger.info("listInputDirOnPath(): " + hdfsService.listInputDirOnPath().toString());
    model.addAttribute("availableDirs", hdfsService.listInputDirOnPath());
    return LAYOUTS_ADMIN;
  }

  @RequestMapping("/infoonly")
  public String indexInfoOnly(Model model) throws Exception {
    //    return "redirect:/admin/home";
    model.addAttribute("view", "input-set-infoonly");
    model.addAttribute("inputSet", new InputSetDtoRequest());
    //    logger.info("listInputDirOnPath(): " + hdfsService.listInputDirOnPath().toString());
    model.addAttribute("availableDirs", hdfsService.listInputDirOnPath());
    return LAYOUTS_ADMIN;
  }

  @RequestMapping("/inputonly")
  public String indexInputOnly(Model model) throws Exception {
    //    return "redirect:/admin/home";
    model.addAttribute("view", "input-set-inputonly");
    model.addAttribute("inputSet", new InputSetDtoRequest());
    //    logger.info("listInputDirOnPath(): " + hdfsService.listInputDirOnPath().toString());
    model.addAttribute("availableDirs", hdfsService.listInputDirOnPath());
    return LAYOUTS_ADMIN;
  }

  @RequestMapping(method = RequestMethod.POST,
      value = "")
  public String postFile(Model model, InputSetDtoRequest inputSetDtoRequest,
      RedirectAttributes redirectAttributes) throws Exception {
    //    logger.info(inputSetDtoRequest.toString());

    if (inputSetDtoRequest.getFilesInput().length == 0
        || inputSetDtoRequest.getFilesInfo().length == 0
        || inputSetDtoRequest.getTypes().size() == 0 || inputSetDtoRequest.getClazz().size() == 0) {
      redirectAttributes.addFlashAttribute("danger", "File error on upload files");
      return "redirect:" + ABSOLUTE_PATH;
    }

    String modelDir;
    if (inputSetDtoRequest.getModelDirSelect().equals("null")) {
      if (inputSetDtoRequest.getModelDir() == null || inputSetDtoRequest.getModelDir().trim()
          .equals("")) {
        redirectAttributes.addFlashAttribute("danger", "Error upload new file");
        return "redirect:" + ABSOLUTE_PATH;
      } else {
        modelDir = inputSetDtoRequest.getModelDir();
        hdfsService.cleanHdfsDir(modelDir);
      }
    } else {
      modelDir = inputSetDtoRequest.getModelDirSelect();
    }

    BufferedReader brInfo = new BufferedReader(
        new InputStreamReader(inputSetDtoRequest.getFilesInfo()[0].getInputStream()));

    int countCols = hdfsService
        .transformAndTransferInfoToHdfsInfo(brInfo, inputSetDtoRequest.getClazz(),
            inputSetDtoRequest.getTypes(), modelDir);

    //    logger.info(
    //        "inputSetDtoRequest.getFilesInput().length = " + inputSetDtoRequest.getFilesInput().length);

    AtomicInteger atomicInteger = new AtomicInteger(1);
    Arrays.stream(inputSetDtoRequest.getFilesInput()).forEach(multipartFile -> {
      //      logger.info("FileName: " + multipartFile.getOriginalFilename());
      try {
        BufferedReader brInput =
            new BufferedReader(new InputStreamReader(multipartFile.getInputStream()));

        BufferedReader brInputLines =
            new BufferedReader(new InputStreamReader(multipartFile.getInputStream()));
        AtomicInteger totalLinesAtomic = new AtomicInteger(0);

        brInputLines.lines().forEach(s -> {
          totalLinesAtomic.incrementAndGet();
        });

        int totalLines = totalLinesAtomic.get();
        hdfsService.transportToHdfs(brInput, modelDir, atomicInteger.getAndIncrement(),
            inputSetDtoRequest.getPercentage(), totalLines, countCols);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
    //    BufferedReader brInput = new BufferedReader(
    //        new InputStreamReader(inputSetDtoRequest.getFilesInput()[0].getInputStream()));
    //    hdfsService.transportToHdfs(brInput, inputSetDtoRequest.getModelDir());

    redirectAttributes.addFlashAttribute("success", "Success upload new file to HDFS");
    return "redirect:" + ABSOLUTE_PATH;
  }

  @RequestMapping(method = RequestMethod.POST,
      value = "/infoonly")
  public String postInfoOnly(Model model, InputSetDtoRequest inputSetDtoRequest,
      RedirectAttributes redirectAttributes) throws Exception {
    if (inputSetDtoRequest.getFilesInput() != null || inputSetDtoRequest.getFilesInfo().length == 0
        || inputSetDtoRequest.getTypes().size() == 0 || inputSetDtoRequest.getClazz().size() == 0) {
      redirectAttributes.addFlashAttribute("danger", "File error on upload files");
      return "redirect:" + ABSOLUTE_PATH;
    }

    String modelDir;
    if (inputSetDtoRequest.getModelDirSelect().equals("null")) {
      if (inputSetDtoRequest.getModelDir() == null || inputSetDtoRequest.getModelDir().trim()
          .equals("")) {
        redirectAttributes.addFlashAttribute("danger", "Error upload new file");
        return "redirect:" + ABSOLUTE_PATH + "/infoonly";
      } else {
        modelDir = inputSetDtoRequest.getModelDir();
        hdfsService.cleanHdfsDir(modelDir);
      }
    } else {
      modelDir = inputSetDtoRequest.getModelDirSelect();
    }

    BufferedReader brInfo = new BufferedReader(
        new InputStreamReader(inputSetDtoRequest.getFilesInfo()[0].getInputStream()));

    int countCols = hdfsService
        .transformAndTransferInfoToHdfsInfo(brInfo, inputSetDtoRequest.getClazz(),
            inputSetDtoRequest.getTypes(), modelDir);

    redirectAttributes.addFlashAttribute("success", "Success upload info file");
    return "redirect:" + ABSOLUTE_PATH + "/infoonly";

  }

  @RequestMapping(method = RequestMethod.POST,
      value = "/inputonly")
  public String postInputOnly(Model model, InputSetDtoRequest inputSetDtoRequest,
      RedirectAttributes redirectAttributes) throws Exception {
    if (inputSetDtoRequest.getFilesInput().length == 0
        || inputSetDtoRequest.getFilesInfo().length == 0 || inputSetDtoRequest.getTypes().size() > 0
        || inputSetDtoRequest.getClazz().size() > 0) {
      redirectAttributes.addFlashAttribute("danger", "File error on upload files");
      logger.error("Error");
      logger.error("inputlength: " + inputSetDtoRequest.getFilesInput().length);
      logger.error("infolength: " + inputSetDtoRequest.getFilesInfo().length);
      logger.error("typesSize: " + inputSetDtoRequest.getTypes().size());
      logger.error("clazzSize: " + inputSetDtoRequest.getClazz().size());
      return "redirect:" + ABSOLUTE_PATH + "/inputonly";
    }

    String modelDir;
    if (inputSetDtoRequest.getModelDirSelect().equals("null")) {
      if (inputSetDtoRequest.getModelDir() == null || inputSetDtoRequest.getModelDir().trim()
          .equals("")) {
        redirectAttributes.addFlashAttribute("danger", "Error upload new file");
        return "redirect:" + ABSOLUTE_PATH;
      } else {
        modelDir = inputSetDtoRequest.getModelDir();
        hdfsService.cleanHdfsDir(modelDir);
      }
    } else {
      modelDir = inputSetDtoRequest.getModelDirSelect();
    }

    BufferedReader brInfo = new BufferedReader(
        new InputStreamReader(inputSetDtoRequest.getFilesInfo()[0].getInputStream()));

    int countCols = (int) brInfo.lines().findFirst().get().split(regex).length;

    AtomicInteger atomicInteger = new AtomicInteger(1);
    Arrays.stream(inputSetDtoRequest.getFilesInput()).forEach(multipartFile -> {
      //      logger.info("FileName: " + multipartFile.getOriginalFilename());
      try {
        BufferedReader brInput =
            new BufferedReader(new InputStreamReader(multipartFile.getInputStream()));

        BufferedReader brInputLines =
            new BufferedReader(new InputStreamReader(multipartFile.getInputStream()));
        AtomicInteger totalLinesAtomic = new AtomicInteger(0);

        brInputLines.lines().forEach(s -> {
          totalLinesAtomic.incrementAndGet();
        });

        int totalLines = totalLinesAtomic.get();
        hdfsService.transportToHdfs(brInput, modelDir, atomicInteger.getAndIncrement(),
            inputSetDtoRequest.getPercentage(), totalLines, countCols);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    redirectAttributes.addFlashAttribute("success", "Success upload input file");
    return "redirect:" + ABSOLUTE_PATH + "/inputonly";

  }
}
