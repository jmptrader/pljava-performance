/**
 * Copyright (c) 2012, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.salesforce.pljava.perf;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.util.Properties;

/**
 * @author hhildebrand
 * 
 */
public class LoadTestData {
    private Connection connection;

    public static void main(String[] argv) throws Exception {
        LoadTestData load = new LoadTestData();
        load.loadEmployees2(100000);
    }

    public LoadTestData() throws Exception {
        Class.forName("org.postgresql.Driver");
        InputStream is = LoadTestData.class.getResourceAsStream("jdbc.properties");
        Properties props = new Properties();
        props.load(is);
        is.close();
        String url = props.getProperty("url");
        connection = DriverManager.getConnection(url, props);
    }

    public void loadEmployees2(int rows) throws Exception {
        ResultSet result = connection.createStatement().executeQuery("select * from perftesting.employees2");
        if (result.next()) {
            return; // don't reload.
        }
        connection.setAutoCommit(false);
        PreparedStatement insert = connection.prepareStatement("INSERT INTO perftesting.employees2 VALUES(?, ?, ?, ?, ?)");
        for (int i = 0; i < rows; i++) {
            insert.setLong(1, i);
            insert.setString(2, String.format("ABCDEFGHIJKLMNO%s", i));
            insert.setInt(3, i);
            insert.setDate(4, new Date(System.currentTimeMillis()));
            insert.setTime(5, new Time(System.currentTimeMillis()));
            insert.executeUpdate();
        }
        connection.commit();
    }
}
