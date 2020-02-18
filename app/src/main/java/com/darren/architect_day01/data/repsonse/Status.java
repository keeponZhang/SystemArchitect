package com.darren.architect_day01.data.repsonse;


/**
 * Created by Deemo on 15/7/20.
 */
public class Status  {
    public int code;
    public String msg;
    public String tips;
    public String time;
    public String sip;
    public String cip;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Status{");
        sb.append("code=").append(code);
        sb.append(", msg='").append(msg).append('\'');
        sb.append(", tips='").append(tips).append('\'');
        sb.append(", time='").append(time).append('\'');
        sb.append(", sip='").append(sip).append('\'');
        sb.append(", cip='").append(cip).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
