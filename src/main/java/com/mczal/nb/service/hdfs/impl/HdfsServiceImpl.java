package com.mczal.nb.service.hdfs.impl;

import com.mczal.nb.service.hdfs.HdfsService;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.Progressable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by mczal on 09/03/17.
 */
@Service
public class HdfsServiceImpl implements HdfsService {

  private static final String HDFS_PATH = "/bayes/";
  private static final String HDFS_PATH_INFO = "/bayes/";
  private static final String HDFS_AUTHORITY = "hdfs://localhost:9000";

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private Configuration conf;

  @Value("${hdfs.input.regex}")
  private String regex;

  private List<String> createMetaInfo(List<String> clazz, List<String> types) {
    List<String> attributes = new ArrayList<String>();
    List<String> classes = new ArrayList<String>();
    clazz.stream().filter(s -> !s.contains("null")).forEach(s -> {
      types.stream()
          .filter(s1 -> s1.split("\\|")[0].equals(s.split("\\|")[0]))
          .forEach(s1 -> {
            if (s.split("\\|")[2].equals("PREDICTOR")) {
              /**
               * Nama|Index|Tipe
               * */
              attributes
                  .add(s.split("\\|")[1] + ","
                      + "" + s1.split("\\|")[0] + ","
                      + "" + s1.split("\\|")[2]);
            } else {
              /**
               * Nama|Index
               * */
              classes.add(s.split("\\|")[1] + ","
                  + "" + s1.split("\\|")[0]);
            }
          });
    });
    String attr = "@attribute :" + String.join(";", attributes);
    String cls = "@class :" + String.join(";", classes);
    List<String> res = new ArrayList<String>();
    res.add(cls);
    res.add(attr);
    return res;
  }

  private boolean checkAvailability(String in, List<String> lists) {
    if (lists.contains(in)) {
      return true;
    }
    return false;
  }

  @Override
  public boolean cleanHdfsDir(String modelDir) throws Exception {
    FileSystem hdfsFileSystem = FileSystem.get(new URI(HDFS_AUTHORITY), conf);
    /* CLEANING HDFS DIRECTORY */
    Path fileInputPath = new Path(HDFS_AUTHORITY + HDFS_PATH + modelDir);
    if (hdfsFileSystem.exists(fileInputPath)) {
      hdfsFileSystem.delete(fileInputPath, true);
    }
    return true;
  }

  @Override
  public List<String> listInputDirOnPath() throws Exception {
    FileSystem fs = FileSystem.get(new URI(HDFS_AUTHORITY), conf);
    FileStatus[] fileStatus = fs.listStatus(new Path(HDFS_AUTHORITY + HDFS_PATH));

    List<String> results = new ArrayList<String>();
    int i = 1;
    for (FileStatus status : fileStatus) {
      String[] splitter = status.getPath().toString().split("bayes");
//      logger.info("splitter: " + Arrays.toString(splitter));
      String currRes = splitter[splitter.length - 1].substring(1);
//      logger.info("currRes: " + currRes);
      String[] currResSplitter = currRes.split("/dataset");
      if (currResSplitter.length > 1) {
        if (!results.contains(currResSplitter[0])) {
          results.add(currResSplitter[0]);
        }
      } else {
        if (!results.contains(currRes)) {
          results.add(currRes);
        }
      }
//      results.add(status.getPath().toString());
    }
    return results;
  }

  @Override
  public int transformAndTransferInfoToHdfsInfo(BufferedReader bufferedReader,
      List<String> clazz, List<String> types, String modelDir)
      throws Exception {
    int countCols = (int) bufferedReader.lines().findFirst().get().split(regex).length;

    FileSystem hdfsFileSystem = FileSystem.get(new URI(HDFS_AUTHORITY), conf);
    /* CLEANING HDFS DIRECTORY */
    Path fileInfo = new Path(HDFS_AUTHORITY + HDFS_PATH_INFO + modelDir + "/info/meta.info");
    if (hdfsFileSystem.exists(fileInfo)) {
      hdfsFileSystem.delete(fileInfo, true);
    }
    List<String> metaInfo = this.createMetaInfo(clazz, types);

    /**
     * Addition per March 20th
     * */
    metaInfo.add("@Count :" + countCols);
    /**
     * Addition per March 20th
     * */

    /**
     * WRITE INFO FILE
     * */
    OutputStream os = hdfsFileSystem.create(fileInfo,
        new Progressable() {
          public void progress() {
            System.out.println("...bytes written");
          }
        });
    BufferedWriter br = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
    metaInfo.forEach(s -> {
      try {
        br.write(s);
        br.newLine();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    br.close();
    hdfsFileSystem.close();

    return countCols;
  }

  @Override
  public boolean transportToHdfs(BufferedReader bufferedReaderInputFile, String modelDir,
      int atomic, int trainPercentage, int totalLines, int countCols) throws Exception {

//    logger.info("IN TRANSPORT TO HDFS");
    FileSystem hdfsFileSystem = FileSystem.get(new URI(HDFS_AUTHORITY), conf);

    AtomicInteger totalLinesAtomic = new AtomicInteger(0);
    logger.info("totalLines: " + totalLines);

    /**
     * WRITE INPUT FILE
     * */
    Date date = new Date(System.currentTimeMillis());
    DateFormat df = new SimpleDateFormat("dd-MM-yyyy_hh-mm");
    Path fileInput = new Path(
        HDFS_AUTHORITY + HDFS_PATH + modelDir + "/input" + "/dataset-" + atomic + "_" + df
            .format(date) + ".in");
    OutputStream osTrainIn = hdfsFileSystem.create(fileInput,
        new Progressable() {
          public void progress() {
            System.out.println("train input file:...bytes written");
          }
        });

    Path fileTestingInput = new Path(
        HDFS_AUTHORITY + HDFS_PATH + modelDir + "/testing/input" + "/dataset-" + atomic + "_" + df
            .format(date) + ".in");
    OutputStream osTesting = hdfsFileSystem.create(fileTestingInput,
        new Progressable() {
          public void progress() {
            System.out.println("testing input file:...bytes written");
          }
        });

    AtomicInteger currLine = new AtomicInteger(1);

    BufferedWriter brTrainInput = new BufferedWriter(new OutputStreamWriter(osTrainIn, "UTF-8"));
    BufferedWriter brTestingInput = new BufferedWriter(new OutputStreamWriter(osTesting, "UTF-8"));

//    double trainPercentageDouble = trainPercentage;
//    logger.info("BEFORE WRITING");

//    while (bufferedReaderInputFile.ready()) {
//      String s = bufferedReaderInputFile.readLine();
////      logger.info("s: " + s);
//      double currPerc = (currLine.getAndIncrement() * 1.0 / totalLines * 1.0) * 100;
////      logger.info("currPerc: " + currPerc);
//      if ((int) currPerc <= trainPercentage) {
////        logger.info("...write train input");
//        brTrainInput.write(s);
//        brTrainInput.newLine();
//      } else {
////        logger.info("... write testing input");
//        brTestingInput.write(s);
//        brTestingInput.newLine();
//      }
//    }
    AtomicInteger trainCount = new AtomicInteger(0);
    AtomicInteger testingCount = new AtomicInteger(0);
    bufferedReaderInputFile.lines().forEach(s -> {
//      logger.info("s: " + s);
      try {
        double currPerc = (currLine.getAndIncrement() * 1.0 / totalLines * 1.0) * 100;
        if (s.split(",").length == countCols) {
          if ((int) currPerc <= trainPercentage) {
            brTrainInput.write(s);
            brTrainInput.newLine();
            trainCount.incrementAndGet();
          } else {
            brTestingInput.write(s);
            brTestingInput.newLine();
            testingCount.incrementAndGet();
          }
        } else {
          logger.info("Ignoring record with missing value detected");
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    brTrainInput.close();
    brTestingInput.close();
    hdfsFileSystem.close();
    logger.info("Input Training Count: " + trainCount.get());
    logger.info("Input Testing Count: " + testingCount.get());
    return true;
  }
}
