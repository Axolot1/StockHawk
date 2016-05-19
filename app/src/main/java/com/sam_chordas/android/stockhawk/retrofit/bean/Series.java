
package com.sam_chordas.android.stockhawk.retrofit.bean;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Series {

    @SerializedName("Timestamp")
    @Expose
    private Integer Timestamp;
    @SerializedName("close")
    @Expose
    private Float close;

    /**
     * 
     * @return
     *     The Timestamp
     */
    public Integer getTimestamp() {
        return Timestamp;
    }

    /**
     * 
     * @param Timestamp
     *     The Timestamp
     */
    public void setTimestamp(Integer Timestamp) {
        this.Timestamp = Timestamp;
    }

    /**
     * 
     * @return
     *     The close
     */
    public Float getClose() {
        return close;
    }

    /**
     * 
     * @param close
     *     The close
     */
    public void setClose(Float close) {
        this.close = close;
    }

}
