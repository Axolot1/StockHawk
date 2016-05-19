
package com.sam_chordas.android.stockhawk.retrofit.bean;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Ranges {

    @SerializedName("close")
    @Expose
    private Close close;

    /**
     * 
     * @return
     *     The close
     */
    public Close getClose() {
        return close;
    }

    /**
     * 
     * @param close
     *     The close
     */
    public void setClose(Close close) {
        this.close = close;
    }

}
