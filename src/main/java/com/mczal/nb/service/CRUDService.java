package com.mczal.nb.service;

import java.util.List;

/**
 * Created by Gl552 on 11/29/2016.
 */
public interface CRUDService<T> {

  void delete(String id) throws Exception;

  T findById(String id) throws Exception;

  List<T> listAll();

  T save(T domainObject) throws Exception;

  T update(T domainObject) throws Exception;
}
