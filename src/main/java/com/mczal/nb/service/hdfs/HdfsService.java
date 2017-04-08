package com.mczal.nb.service.hdfs;

import java.io.BufferedReader;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by mczal on 09/03/17.
 */
public interface HdfsService {

  boolean cleanHdfsDir(String modelDir) throws Exception;

  BufferedReader getOutputModelBufferedReaderFromModelHdfs(String modelHdfs) throws Exception;

  List<String> listInputDirOnPath() throws Exception;

  int transformAndTransferInfoToHdfsInfo(BufferedReader bufferedReader, List<String> clazz,
      List<String> types, String modelDir) throws Exception;

  boolean transportToHdfs(BufferedReader bufferedReader, String modelDir, int atomic,
      int trainPercentage, int totalLines, int countCols)
      throws Exception;

}
