package pers.liuhaoan.jvtcGroupOfRobot.domain;

import java.util.List;

public class PageBean<T> {
    //分页任务信息对象
    private int totalCount;     //总记录数
    private int totalPage;      //总页数
    private int currentPage;    //当前页数
    private int pageSize;       //每页显示的条数

    private List<T> list;       //每页显示的数据集合

    public PageBean() {
    }

    public PageBean(int totalCount, int totalPage, int currentPage, int pageSize, List<T> list) {
        this.totalCount = totalCount;
        this.totalPage = totalPage;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.list = list;
    }

    @Override
    public String toString() {
        return "PageBean{" +
                "totalCount=" + totalCount +
                ", totalPage=" + totalPage +
                ", currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                ", list=" + list +
                '}';
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public List<T> getList() {
        return list;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
