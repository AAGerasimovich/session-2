package ru.sbt.jschool.session2;

import java.util.Date;

public interface Formatter {
    String getDateFormat(Date date);
    String getMoneyFormat(Double d,  int len);
    String getNumberFormat(int n,  int len);
    String getStringFormat(String str, int len);

}
