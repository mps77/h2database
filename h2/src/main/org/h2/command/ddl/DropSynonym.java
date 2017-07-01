/*
 * Copyright 2004-2014 H2 Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (http://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 */
package org.h2.command.ddl;

import org.h2.api.ErrorCode;
import org.h2.command.CommandInterface;
import org.h2.constraint.ConstraintReferential;
import org.h2.engine.Database;
import org.h2.engine.DbObject;
import org.h2.engine.Right;
import org.h2.engine.Session;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.Table;
import org.h2.table.TableType;
import org.h2.table.TableView;

/**
 * This class represents the statement
 * DROP SYNONYM
 */
public class DropSynonym extends SchemaCommand {

    private String synonymName;
    private boolean ifExists;

    public DropSynonym(Session session, Schema schema) {
        super(session, schema);
    }

    public void setSynonymName(String name) {
        this.synonymName = name;
    }

    @Override
    public int update() {
        session.commit(true);
        Table synonym = getSchema().findTableOrView(session, synonymName);
        if (synonym == null) {
            if (!ifExists) {
                throw DbException.get(ErrorCode.TABLE_OR_VIEW_NOT_FOUND_1, synonymName);
            }
        } else {
            if (!TableType.SYNONYM.equals(synonym.getTableType())) {
                throw DbException.get(ErrorCode.TABLE_OR_VIEW_NOT_FOUND_1, synonymName);
            }
            session.getUser().checkRight(synonym, Right.ALL);
            synonym.lock(session, true, true);
            session.getDatabase().removeSchemaObject(session, synonym);
        }
        return 0;
    }

    public void setIfExists(boolean ifExists) {
        this.ifExists = ifExists;
    }

    @Override
    public int getType() {
        return CommandInterface.DROP_SYNONYM;
    }

}
