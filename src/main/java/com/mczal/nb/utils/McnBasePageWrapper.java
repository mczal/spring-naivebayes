package com.mczal.nb.utils;

/**
 * Created by mczal on 17/03/17.
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;

/**
 * Created by Gl552 on 12/10/2016.
 */
public class McnBasePageWrapper<T> implements Serializable {

  public static final Integer MAX_PAGE_ITEM_DISPLAY = 5;

  private static final long serialVersionUID = -3703087758012717980L;

  private int currentNumber;

  private List<PageItem> items;

  private Page<T> page;

  private String url;

  public McnBasePageWrapper(Page<T> page, String url) {
    this.page = page;
    this.url = url;
    items = new ArrayList<PageItem>();

    currentNumber = page.getNumber() + 1; //start from 1 to match page.page

    int start, size;
    if (page.getTotalPages() <= MAX_PAGE_ITEM_DISPLAY) {
      start = 1;
      size = page.getTotalPages();
    } else {
      if (currentNumber <= MAX_PAGE_ITEM_DISPLAY - MAX_PAGE_ITEM_DISPLAY / 2) {
        start = 1;
        size = MAX_PAGE_ITEM_DISPLAY;
      } else if (currentNumber >= page.getTotalPages() - MAX_PAGE_ITEM_DISPLAY / 2) {
        start = page.getTotalPages() - MAX_PAGE_ITEM_DISPLAY + 1;
        size = MAX_PAGE_ITEM_DISPLAY;
      } else {
        start = currentNumber - MAX_PAGE_ITEM_DISPLAY / 2;
        size = MAX_PAGE_ITEM_DISPLAY;
      }
    }

    for (int i = 0; i < size; i++) {
      items.add(new PageItem(start + i, (start + i) == currentNumber));
    }
  }


  public class PageItem {

    private boolean current;

    private int number;

    public PageItem(int number, boolean current) {
      this.number = number;
      this.current = current;
    }

    public int getNumber() {
      return this.number;
    }

    public boolean isCurrent() {
      return this.current;
    }

    @Override
    public String toString() {
      return "PageItem{" +
          "current=" + current +
          ", number=" + number +
          '}';
    }
  }

  public List<T> getContent() {
    return page.getContent();
  }

  public int getCurrentNumber() {
    return currentNumber;
  }

  public void setCurrentNumber(int currentNumber) {
    this.currentNumber = currentNumber;
  }

  public List<PageItem> getItems() {
    return items;
  }

  public void setItems(List<PageItem> items) {
    this.items = items;
  }

  public int getNumber() {
    return currentNumber;
  }

  public Page<T> getPage() {
    return page;
  }

  public void setPage(Page<T> page) {
    this.page = page;
  }

  public int getSize() {
    return page.getSize();
  }

  public int getTotalPages() {
    return page.getTotalPages();
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public boolean isFirstPage() {
    return page.isFirst();
  }

  public boolean isHasNextPage() {
    return page.hasNext();
  }

  public boolean isHasPreviousPage() {
    return page.hasPrevious();
  }

  public boolean isLastPage() {
    return page.isLast();
  }

  @Override
  public String toString() {
    return "McnBasePageWrapper{" +
        "currentNumber=" + currentNumber +
        ", items=" + items +
        ", page=" + page +
        ", url='" + url + '\'' +
        '}';
  }
}
