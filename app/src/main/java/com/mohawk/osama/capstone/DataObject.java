package com.mohawk.osama.capstone;

/**
 * Created by Osama on 9/17/2016.
 */
public class DataObject {
    private String mText1;
    private String mText2;
    private String mText3;

    /**
     * Instantiates a new Data object.
     *
     * @param text1 the text 1
     * @param text2 the text 2
     * @param text3 the text 3
     */
    DataObject (String text1, String text2, String text3){
        mText1 = text1;
        mText2 = text2;
        mText3 = text3;
    }

    /**
     * Gets text 1.
     *
     * @return the text 1
     */
    public String getmText1() {
        return mText1;
    }

    /**
     * Sets text 1.
     *
     * @param mText1 the m text 1
     */
    public void setmText1(String mText1) {
        this.mText1 = mText1;
    }

    /**
     * Gets text 2.
     *
     * @return the text 2
     */
    public String getmText2() {
        return mText2;
    }

    /**
     * Sets text 2.
     *
     * @param mText2 the m text 2
     */
    public void setmText2(String mText2) {
        this.mText2 = mText2;
    }

    /**
     * Gets text 3.
     *
     * @return the text 3
     */
    public String getmText3() {
        return mText3;
    }

    /**
     * Sets text 3.
     *
     * @param mText3 the m text 3
     */
    public void setmText3(String mText3) {
        this.mText3 = mText3;
    }
}
