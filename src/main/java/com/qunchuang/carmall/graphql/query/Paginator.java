package com.qunchuang.carmall.graphql.query;

import com.qunchuang.carmall.graphql.annotation.SchemaDocumentation;

/**
 * Paginator
 *
 * @author zzk
 * @date 2018/10/25
 */
@SchemaDocumentation("分页器")
public class Paginator {
    private int page;
    private int size;

    public Paginator() {
    }

    public Paginator(int page, int size) {
        this.page = page;
        this.size = size;
    }

    @SchemaDocumentation("当前页号（从1开始）")
    public void setPage(int page) {
        this.page = page;
    }

    @SchemaDocumentation("每页大小")
    public void setSize(int size) {
        this.size = size;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

}
