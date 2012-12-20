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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author hhildebrand
 * 
 */
public class Benchmark {
    private static class InDatabase {
        private static final Benchmark SINGLETON;

        static {
            try {
                SINGLETON = new Benchmark();
            } catch (SQLException e) {
                log.log(Level.SEVERE, "Cannot initialize Benchmark singleton",
                        e);
                throw new IllegalStateException(e);
            }
        }
    }

    private final static Logger log = Logger.getLogger(Benchmark.class.getCanonicalName());

    public static long benchmark_select() throws SQLException {
        Benchmark benchmark = InDatabase.SINGLETON;
        long then = System.currentTimeMillis();
        benchmark.benchmarkSelect();
        return System.currentTimeMillis() - then;
    }

    private final Connection connection;

    public Benchmark(Connection connection) {
        this.connection = connection;
    }

    private Benchmark() throws SQLException {
        this(DriverManager.getConnection("jdbc:default:connection"));
    }

    public void benchmarkSelect() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet rslt = statement.executeQuery("select e.* from perftesting.employees2 as e");
        while (rslt.next()) {
            rslt.getLong("id");
            rslt.getString("name");
            rslt.getInt("salary");
            rslt.getDate("transferDay");
            rslt.getTime("transferTime");
        }
        statement.close();
    }
}
