
package com.sam_chordas.android.stockhawk.retrofit.bean;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Close {

    @SerializedName("min")
    @Expose
    private Double min;
    @SerializedName("max")
    @Expose
    private Double max;

    /**
     * 
     * @return
     *     The min
     */
    public Double getMin() {
        return min;
    }

    /**
     * 
     * @param min
     *     The min
     */
    public void setMin(Double min) {
        this.min = min;
    }

    /**
     * 
     * @return
     *     The max
     */
    public Double getMax() {
        return max;
    }

    /**
     * 
     * @param max
     *     The max
     */
    public void setMax(Double max) {
        this.max = max;
    }

}
