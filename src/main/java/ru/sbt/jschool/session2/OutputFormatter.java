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
import java.util.*;


/**
 */
public class OutputFormatter {




    private PrintStream out;
    private String sepH = "-";
    private String sepV = "|";
    private String sepC = "+";
    private String[] types;
    private int[] maxLenColm;


    public OutputFormatter(PrintStream out) {
        this.out = out;
    }



    private void setMaxLenColms(String[] names, Object[][] data) {

        maxLenColm = new int[names.length];
        types = new String[names.length];

        for (int i = 0; i < names.length; ++i) {
                maxLenColm[i] = names[i].length();

        }

        for (int i = 0; i < data.length; ++i){

            for (int j = 0; j < names.length; ++j) {
                if (data[i][j] instanceof String) {
                    String s = (String) data[i][j];
                    if (maxLenColm[j] < s.length()) {
                        maxLenColm[j] = s.length();
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
                    if (maxLenColm[j] < 10) {
                        maxLenColm[j] = 10;
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
        this.setMaxLenColms(names, data);
        String separator = sepC;
        for (int i = 0; i< maxLenColm.length; ++i){
            for(int j = 0; j < maxLenColm[i]; ++j){
                separator += sepH;
            }
            separator += sepC;
        }
        this.out.println(separator);
        this.out.print(sepV);

        for(int j = 0; j < names.length; ++j) {
            String s = names[j];
            int k = (maxLenColm[j]-names[j].length())/2;
            while (k>0){
                s =" "  +  s;
                k--;
            }
            String  f = "%" + -maxLenColm[j]+ "s|";
            this.out.printf(f, s);
        }
        this.out.println("");
        for(int i = 0; i < data.length; ++i){

            this.out.println(separator);
            this.out.print(sepV);

            for(int j = 0; j < names.length; ++j) {

                swichPrint(data[i][j], j);

            }
            this.out.println("");
        }
        this.out.println(separator);

    }

    private  void swichPrint(Object data, int i){

        if (data instanceof String) {
            stringPrint((String) data, i);
        }else
        if (data instanceof Integer) {
            numberPrint((int) data, i);
        }else
        if (data instanceof Date) {
            datePrint((Date) data, i);
        }else
        if (data instanceof Double) {
            moneyPrint((Double) data, i);
        }else
            nullPrint(i);


        }

    private void numberPrint(int num, int i) {

        String format = "%," + maxLenColm[i] + "d" +"%s";
        this.out.printf(format, num , sepV);

    }

    private void datePrint(Date d, int i){

        Formatter f = new Formatter();
        f.format("%td%s%tm%s%tY|", d, ".",d,"." ,d);
        this.out.print(f);
    }

    private void moneyPrint(Double d, int i){
        d = new BigDecimal(d).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
        String s = "%," + maxLenColm[i] +".2f%s";
        Formatter f = new Formatter();
        f.format(s, d, sepV);

        this.out.print(f);
    }
    private void stringPrint(String str, int i) {

        String format = "%" + -maxLenColm[i] + "s" + "%s";
        this.out.printf(format,  str , sepV);

    }
    private void nullPrint(int i) {
        String format;
        if (types[i].equals("money") || types[i].equals("number")){
             format = "%" + maxLenColm[i] + "s%s";
        } else  format = "%" + -maxLenColm[i] + "s%s";


        this.out.printf(format,  "-", sepV);

    }


}
