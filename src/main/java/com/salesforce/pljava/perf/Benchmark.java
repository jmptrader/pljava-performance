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
import java.sql.PreparedStatement;
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

    public static void benchmark_empty() {

    }

    public static void benchmark_select() throws SQLException {
        Benchmark benchmark = InDatabase.SINGLETON;
        benchmark.benchmarkSelect();
    }

    public static void benchmark_select_and_insert() throws SQLException {
        Benchmark benchmark = InDatabase.SINGLETON;
        benchmark.benchmarkSelectAndInsert();
    }

    public static void benchmark_select_one() throws SQLException {
        Benchmark benchmark = InDatabase.SINGLETON;
        benchmark.benchmarkSelectOne();
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
            rslt.getLong(1);
            rslt.getString(2);
            rslt.getInt(3);
            rslt.getDate(4);
            rslt.getTime(5);
        }
        statement.close();
    }

    public void benchmarkSelectAndInsert() throws SQLException {
        long nextId = 0;
        ResultSet r = connection.createStatement().executeQuery("SELECT MAX (e.id) FROM perftesting.employees3 as e");
        if (r.next()) {
            nextId = r.getLong(1);
        }
        Statement statement = connection.createStatement();
        PreparedStatement ps = connection.prepareStatement("INSERT INTO perftesting.employees3 VALUES(?, ?, ?, ?, ?)");
        ResultSet rslt = statement.executeQuery("select e.* from perftesting.employees2 as e");
        while (rslt.next()) {
            ps.setLong(1, nextId + rslt.getLong(1) + 1);
            ps.setString(2, rslt.getString(2));
            ps.setInt(3, rslt.getInt(3));
            ps.setDate(4, rslt.getDate(4));
            ps.setTime(5, rslt.getTime(5));
            ps.execute();
        }
        ps.close();
        statement.close();
    }

    public void benchmarkSelectOne() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet rslt = statement.executeQuery("select e.* from perftesting.employees2 as e where e.id = 0");
        while (rslt.next()) {
            rslt.getLong(1);
            rslt.getString(2);
            rslt.getInt(3);
            rslt.getDate(4);
            rslt.getTime(5);
        }
        statement.close();
    }
}
