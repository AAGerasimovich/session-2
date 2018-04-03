/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.sbt.jschool.session2;


import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 */
public class OutputFormatter  implements DateFormat, NumberFormat, MoneyFormat, StringFormat{


    private PrintStream out;
    private String sepH = "-";
    private String sepV = "|";
    private String sepC = "+";

    public OutputFormatter(PrintStream out) {
        this.out = out;
    }

    private void setMaxLenColms(String[] names, Object[][] data, int[] maxLenColm, String[] types) {

        for (int i = 0; i < names.length; ++i) {
                maxLenColm[i] = names[i].length();
        }

        for (int i = 0; i < data.length; ++i){

            for (int j = 0; j < names.length; ++j) {
                if (data[i][j] instanceof String) {
                    String s = (String) data[i][j];
                    if (s.length()<15) {
                        if (maxLenColm[j] < s.length() ) {
                            maxLenColm[j] = s.length();
                        }
                    } else {
                        maxLenColm[j] = 15;
                    }
                    if (types[j] == null) {
                        types[j] = "string";
                    }
                }

                if (data[i][j] instanceof Integer) {
                    Integer n = (Integer) data[i][j];
                    maxLenColm[j] = Math.max(maxLenColm[j], getLenNumber(n));
                    if (types[j] == null) {
                        types[j] = "number";
                    }
                }

                if (data[i][j] instanceof Double) {
                    Double d = (Double) data[i][j];
                    Integer n = d.intValue();
                    maxLenColm[j] = Math.max(maxLenColm[j], getLenNumber(n)+3);
                    if (types[j] == null) {
                        types[j] = "money";
                    }
                }

                if (data[i][j] instanceof Date) {
                    if (maxLenColm[j] < "dd.MM.yyyy HH:mm.SS.sss".length()) {
                        maxLenColm[j] = "dd.MM.yyyy HH:mm.SS.sss".length();
                    }
                    if (types[j] == null) {
                        types[j] = "date";
                    }
                }
            }
        }
    }

    private int getLenNumber(Integer n){
        int len;

        if (n.toString().length()>3){
            len = n.toString().length() + n.toString().length()/3;
        } else {
            len = n.toString().length();
        }
        if (n.toString().length()%3 == 0 && len>3){
            len--;
        }
        return len;

    }
    public void output(String[] names, Object[][] data) {

        int[] maxLenColm = new int[names.length];
        String[] types = new String[names.length];

        this.setMaxLenColms(names, data, maxLenColm, types);

        StringBuilder s = new StringBuilder();

        String separator;
        s.append(sepC);
        for (int i = 0; i< maxLenColm.length; ++i){
            for(int j = 0; j < maxLenColm[i]; ++j){
                s.append(sepH);
            }
            s.append(sepC);
        }
        separator = s.toString();
        this.out.println(separator);
        this.out.print(sepV);

        for(int j = 0; j < names.length; ++j) {
            StringBuilder name = new StringBuilder();
            int k = (maxLenColm[j]-names[j].length())/2;
            while (k>0){
                name.append(" ");
                k--;
            }
            name.append(names[j]);
            String  f = "%" + -maxLenColm[j]+ "s|";
            this.out.printf(f, name.toString());
        }
        this.out.println("");
        for(int i = 0; i < data.length; ++i){

            this.out.println(separator);
            this.out.print(sepV);

            for(int j = 0; j < names.length; ++j) {
                swichPrint(data[i][j], maxLenColm[j], types[j]);
                this.out.print(sepV);
            }
            this.out.println("");
        }
        this.out.println(separator);
    }

    private  void swichPrint(Object data, int len, String type ){

        if (data instanceof String) {
            this.out.print(getStringFormat((String) data, len));
        }else
        if (data instanceof Integer) {
            this.out.print(getNumberFormat((int) data, len));
        }else
        if (data instanceof Date) {
            this.out.print(getDateFormat((Date) data));
        }else
        if (data instanceof Double) {
            this.out.print(getMoneyFormat((Double) data, len));
        }else
            nullPrint(type, len);
        }

    public String getNumberFormat(int num, int i) {

        String format = "%," + i + "d";
        return String.format(format, num );
    }

    public String getDateFormat(Date d){

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm.SS.sss");
        System.out.println();

//        Formatter f = new Formatter();
//        f.format("%td%s%tm%s%tY|", d, ".",d,"." ,d);
        return (dateFormat.format(d));
    }

    public String getMoneyFormat(Double d, int i){
        d = new BigDecimal(d).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
        String s = "%," + i +".2f";
        Formatter f = new Formatter();
        f.format(s, d);

        return f.toString();
    }

    public String getStringFormat(String str, int i) {

        String format = "%" + -i + "s" ;
        if (str.length()<=15) {
            return String.format(format, str);
        } else {
            return (str.subSequence(0, 12)+"...");
        }
    }

    private void nullPrint(String type, int len) {
        String format;
        if (type.equals("money") || type.equals("number")){
             format = "%" + len + "s";
        } else  format = "%" + -len + "s";

        this.out.printf(format,  "-");
    }
}
