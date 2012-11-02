/*
 * Copyright 2007 Penn State University
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.psu.citeseerx.dao;

import java.util.HashMap;
import java.util.Collection;
import java.sql.*;

/**
 * Utility for maintaining a group of instructions that should be
 * executed in batch.  An arbitrary number of prepared statements can
 * be cached here, and each batch statement will be executed on the call
 * to "executeAll".  Clients are responsible for handling the statements
 * properly, using addBatch to add new input sets and not executing
 * intermediary instructions.  This class is for SQL updates only!
 * 
 * This class is not thread safe.
 * 
 * @author Isaac Councill
 *
 */
public class BatchInstructionBundle {

    private final HashMap<String,PreparedStatement> stmtCache =
        new HashMap<String,PreparedStatement>();
    
    private final Connection con;
    
    public BatchInstructionBundle(Connection con) {
        this.con = con;
    }
    
    public Connection getConnection() {
        return con;
    }
    
    public PreparedStatement getStatement(String sql) throws SQLException {
        if (stmtCache.containsKey(sql)) {
            return stmtCache.get(sql);
        } else {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmtCache.put(sql, stmt);
            return stmt;
        }
        
    }  //- getStatement
    
    
    public void executeAll() throws SQLException {
        Collection<PreparedStatement> stmts = stmtCache.values();
        try {
            for (PreparedStatement stmt : stmts) {
                stmt.executeBatch();
            }
        } catch (SQLException e) {
            throw(e);
        } finally {
            for (PreparedStatement stmt : stmts) {
                try { stmt.clearBatch(); } catch (Exception e) { }
                try { stmt.close(); } catch (Exception e) { }
            }
        }
        
    }  //- executeAll
    
}  //- class BatchInstructionBundle 
