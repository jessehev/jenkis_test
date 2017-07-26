package com.utstar.baseplayer.utils.inf;

import java.io.IOException;

/**
 * 数据响应接口
 */
public interface IResponse {
    /**
     * 响应的数据回调
     */
    public void onResponse(String data);

    /**
     * 错误返回回掉
     */
    public void onError(IOException e);
}