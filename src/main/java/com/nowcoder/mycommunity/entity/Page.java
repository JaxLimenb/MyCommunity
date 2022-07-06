package com.nowcoder.mycommunity.entity;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-06-9:41
 * 封装分页相关信息
 */
public class Page {

    // 当前页码
    private int current = 1;
    // 显示数据上限
    private int limit = 10;
    // 数据总数（用于计算总的页数）
    private int rows;
    // 查询路劲（用来复用分页的链接）
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current >= 1)
            this.current = current;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit >= 1 && limit <= 100)
            this.limit = limit;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0)
            this.rows = rows;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 根据当前页码(current)和每页显示几条数据(limit)，来获取当前页的起始行
     * @return 返回起始行offset
     */
    public int getOffset() {
        return (current - 1) * limit;
    }

    /**
     * 根据总行数(rows)及每页显示数据数(limit)获取总页数
     * @return 返回总页数，如果rows整除limit返回rows/limit，否则返回rows/limit + 1
     */
    public int getTotal() {
        return rows / limit + (rows % limit == 0 ? 0 : 1);
    }

    /**
     * 获取起始页码，网页中页码太多不会全部显示，只会显示当前页码的前两页和后两页
     * @return 返回当前页码往前的两页，如果小于等于0返回1
     */
    public int getFrom() {
        return Math.max(current - 2, 1);
    }

    /**
     * 获取结束页码，当前页两页后的页码
     * @return 如果超过最大页码返回最大页码
     */
    public int getTo() {
        return Math.min(current + 2, getTotal());
    }
}
