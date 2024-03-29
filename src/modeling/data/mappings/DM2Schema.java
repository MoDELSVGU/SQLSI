/**************************************************************************
Copyright 2019 Vietnamese-German-University
Copyright 2023 ETH Zurich

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

@author: hoangnguyen (hoang.nguyen@inf.ethz.ch)
***************************************************************************/

package modeling.data.mappings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import modeling.data.entities.Association;
import modeling.data.entities.Attribute;
import modeling.data.entities.DataModel;
import modeling.data.entities.End;
import modeling.data.entities.End_AssociationClass;
import modeling.data.entities.Entity;
import modeling.data.utils.SQLStatementHelper;
import modeling.mysql.MySQLConstraint;
import modeling.security.entities.SecurityModel;
import modeling.security.utils.SQLSIUtils;
import modeling.statements.create.CreateDatabase;
import modeling.statements.drop.DropDatabase;
import net.sf.jsqlparser.schema.Database;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.UseStatement;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.alter.AlterExpression;
import net.sf.jsqlparser.statement.alter.AlterOperation;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;

public class DM2Schema {

    public static String generateDatabase(DataModel context, SecurityModel sm, String databaseName) {
        List<String> schema = new ArrayList<String>();

        List<String> DBStatements = generateDBStatements(databaseName);
        schema.addAll(DBStatements);

        List<Statement> entityStatements = generateEntityStatements(context, sm);
        schema.addAll(entityStatements.stream().map(Statement::toString).collect(Collectors.toList()));

        List<Statement> associationStatements = generateAssociationStatements(context);
        schema.addAll(associationStatements.stream().map(Statement::toString).collect(Collectors.toList()));
        
        List<Statement> associationClassStatements = generateAssociationClassStatements(context, sm);
        schema.addAll(associationClassStatements.stream().map(Statement::toString).collect(Collectors.toList()));

        String script = "";

        for (String schemata : schema) {
            script += SQLStatementHelper.transform(schemata);
        }
        return script;
    }

    private static List<Statement> generateAssociationClassStatements(DataModel dataModel, SecurityModel securityModel) {
        Map<String, Entity> mapEntities = dataModel.getEntities();
        List<Entity> entities = new ArrayList<Entity>();
        for (Entry<String, Entity> m : mapEntities.entrySet()) {
            Entity e = m.getValue();
            if (e.isAssociation()) {
                entities.add(e);
            }
        }
        return createTablesStatements(entities, securityModel);
    }

    private static List<Statement> generateAssociationStatements(DataModel dataModel) {
        return createAssociationStatements(dataModel.getAssociations());
    }

    private static List<Statement> generateEntityStatements(DataModel dataModel, SecurityModel securityModel) {
        Map<String, Entity> mapEntities = dataModel.getEntities();
        List<Entity> entities = new ArrayList<Entity>();
        for (Entry<String, Entity> m : mapEntities.entrySet()) {
            Entity e = m.getValue();
            if (!e.isAssociation()) {
                entities.add(e);
            }
        }
        return createTablesStatements(entities, securityModel);
    }

    private static List<String> generateDBStatements(String databaseName) {
        List<String> dbstatements = new ArrayList<String>();
        Database database = new Database(databaseName);

        DropDatabase dropDatabase = new DropDatabase();
        dropDatabase.setName(database);
        dropDatabase.setIfExists(true);
        dbstatements.add(dropDatabase.toString());

        CreateDatabase createDatabase = new CreateDatabase();
        createDatabase.setDatabase(database);
        dbstatements.add(createDatabase.toString());

        UseStatement useDatabase = new UseStatement(database.getDatabaseName());
        dbstatements.add(useDatabase.toString());
        return dbstatements;
    }

    private static List<Statement> createAssociationStatements(Set<Association> associations) {
        List<Statement> associationTables = new ArrayList<Statement>();
        for (Association association : associations) {
            if (association.isManyToMany())
                associationTables.addAll(createAssociationTable(association));
            else if (association.isManyToOne()) {
                associationTables.addAll(createReferences(association));
            } else if (association.isOneToOne()) {
                associationTables.addAll(createBothSidesReferences(association));
            }
        }
        return associationTables;
    }

    private static List<Statement> createBothSidesReferences(Association association) {
        End leftEnd = association.getLeftEnd();
        End rightEnd = association.getRightEnd();

        Table leftTable = new Table(leftEnd.getTargetClass());
        List<Statement> foreignReferences = new ArrayList<Statement>();
        Alter foreignLeftKeys = new Alter();
        foreignReferences.add(foreignLeftKeys);
        foreignLeftKeys.setTable(leftTable);
        AlterExpression addLeftColumnExpression = new AlterExpression();
        AlterExpression referenceLeftExpression = new AlterExpression();
        foreignLeftKeys.setAlterExpressions(Arrays.asList(addLeftColumnExpression, referenceLeftExpression));
        addLeftColumnExpression.setOperation(AlterOperation.ADD);
        ColDataType referenceLeftColumnDataType = new ColDataType();
        referenceLeftColumnDataType.setDataType("INT");
        referenceLeftColumnDataType.setArgumentsStringList(Arrays.asList("11"));
        addLeftColumnExpression.addColDataType(leftEnd.getOpp(), referenceLeftColumnDataType);
        referenceLeftExpression.setOperation(AlterOperation.ADD);
        referenceLeftExpression.setFkColumns(Arrays.asList(leftEnd.getOpp()));
        referenceLeftExpression.setFkSourceColumns(Arrays.asList(String.format("%1$s_id", leftEnd.getCurrentClass())));
        referenceLeftExpression.setFkSourceTable(leftEnd.getCurrentClass());
        Alter foreignRightKeys = new Alter();
        foreignReferences.add(foreignRightKeys);

        Table rightTable = new Table(rightEnd.getTargetClass());
        foreignRightKeys.setTable(rightTable);
        AlterExpression addRightColumnExpression = new AlterExpression();
        AlterExpression referenceRightExpression = new AlterExpression();
        foreignRightKeys.setAlterExpressions(Arrays.asList(addRightColumnExpression, referenceRightExpression));
        addRightColumnExpression.setOperation(AlterOperation.ADD);
        ColDataType referenceRightColumnDataType = new ColDataType();
        referenceRightColumnDataType.setDataType("INT");
        referenceRightColumnDataType.setArgumentsStringList(Arrays.asList("11"));
        addRightColumnExpression.addColDataType(rightEnd.getOpp(), referenceRightColumnDataType);
        referenceRightExpression.setOperation(AlterOperation.ADD);
        referenceRightExpression.setFkColumns(Arrays.asList(rightEnd.getOpp()));
        referenceRightExpression
                .setFkSourceColumns(Arrays.asList(String.format("%1$s_id", rightEnd.getCurrentClass())));
        referenceRightExpression.setFkSourceTable(rightEnd.getCurrentClass());

        return foreignReferences;
    }

    private static List<Statement> createReferences(Association association) {
        End manyEnd = association.getManyEnd();
        Table table = new Table(manyEnd.getTargetClass());
        List<Statement> foreignReferences = new ArrayList<Statement>();
        Alter foreignKeys = new Alter();
        foreignReferences.add(foreignKeys);
        foreignKeys.setTable(table);
        AlterExpression addColumnExpression = new AlterExpression();
        AlterExpression referenceExpression = new AlterExpression();
        foreignKeys.setAlterExpressions(Arrays.asList(addColumnExpression, referenceExpression));
        addColumnExpression.setOperation(AlterOperation.ADD);
        ColDataType referenceColumnDataType = new ColDataType();
        referenceColumnDataType.setDataType("INT");
        referenceColumnDataType.setArgumentsStringList(Arrays.asList("11"));
        addColumnExpression.addColDataType(manyEnd.getOpp(), referenceColumnDataType);
        referenceExpression.setOperation(AlterOperation.ADD);
        referenceExpression.setFkColumns(Arrays.asList(manyEnd.getOpp()));
        referenceExpression.setFkSourceColumns(Arrays.asList(String.format("%1$s_id", manyEnd.getCurrentClass())));
        referenceExpression.setFkSourceTable(manyEnd.getCurrentClass());
        return foreignReferences;
    }

    private static List<Statement> createAssociationTable(Association association) {
        List<Statement> createAssocTableStatements = new ArrayList<Statement>();
        CreateTable createAssociation = new CreateTable();
        createAssocTableStatements.add(createAssociation);
        // Get association name
        Table table = new Table(association.getName());
        createAssociation.setTable(table);
        // Get association-ends
        List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
        createAssociation.setColumnDefinitions(columns);
        // Add association-ends
        ColumnDefinition leftColumn = createAssociationColumn(association.getLeftEnd().getOpp());
        columns.add(leftColumn);
        ColumnDefinition rightColumn = createAssociationColumn(association.getRightEnd().getOpp());
        columns.add(rightColumn);

        Alter foreignKeys = new Alter();
        createAssocTableStatements.add(foreignKeys);
        foreignKeys.setTable(table);
        AlterExpression leftExpression = new AlterExpression();
        AlterExpression rightExpression = new AlterExpression();
        foreignKeys.setAlterExpressions(Arrays.asList(leftExpression, rightExpression));

        leftExpression.setOperation(AlterOperation.ADD);
        leftExpression.setFkColumns(Arrays.asList(association.getLeftEnd().getOpp()));
        leftExpression.setFkSourceColumns(
                Arrays.asList(String.format("%1$s_id", association.getLeftEnd().getCurrentClass())));
        leftExpression.setFkSourceTable(association.getLeftEnd().getCurrentClass());

        rightExpression.setOperation(AlterOperation.ADD);
        rightExpression.setFkColumns(Arrays.asList(association.getRightEnd().getOpp()));
        rightExpression.setFkSourceColumns(
                Arrays.asList(String.format("%1$s_id", association.getRightEnd().getCurrentClass())));
        rightExpression.setFkSourceTable(association.getRightEnd().getCurrentClass());
        return createAssocTableStatements;
    }

    private static ColumnDefinition createAssociationColumn(String name) {
        ColumnDefinition column = new ColumnDefinition();
        column.setColumnName(name);
        // Set column type
        ColDataType colDataType = new ColDataType();
        colDataType.setDataType("VARCHAR");
        colDataType.setArgumentsStringList(Arrays.asList("100"));
        column.setColDataType(colDataType);
        column.addColumnSpecs(Arrays.asList(MySQLConstraint.NOT_NULL));
        return column;
    }

    private static List<Statement> createTablesStatements(List<Entity> entities, SecurityModel sm) {
        List<Statement> tables = new ArrayList<Statement>();
        for (Entity entity : entities) {
            CreateTable createTable = new CreateTable();
            tables.add(createTable);
            // Set table name
            Table table = new Table(entity.getName());
            createTable.setTable(table);
            // Set columns
            List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
            createTable.setColumnDefinitions(columns);
            // Add id_column
            ColumnDefinition idColumn = createIdentityColumn(table);
            columns.add(idColumn);
            // If this is an association class
            if (entity.isAssociation()) {
                Set<End_AssociationClass> end_ascs = entity.getEnd_acs();
                for (End_AssociationClass end_asc : end_ascs) {
                    ColumnDefinition columnDef = createAssociationColumn(end_asc.getName());
                    columns.add(columnDef);

                    Alter foreignKeys = new Alter();
                    tables.add(foreignKeys);
                    foreignKeys.setTable(table);
                    AlterExpression alterExpression = new AlterExpression();
                    foreignKeys.addAlterExpression(alterExpression);

                    alterExpression.setOperation(AlterOperation.ADD);
                    alterExpression.setFkColumns(Arrays.asList(end_asc.getName()));
                    alterExpression.setFkSourceColumns(
                            Arrays.asList(String.format("%1$s_id", end_asc.getTargetClass())));
                    alterExpression.setFkSourceTable(end_asc.getTargetClass());
                }
            }
            // Add other attributes
            for (Attribute attribute : entity.getAttributes()) {
                ColumnDefinition column = createAttributeColumn(attribute);
                columns.add(column);
            }
            if (SQLSIUtils.isUserClass(entity, sm)) {
                ColumnDefinition roleColumn = createRoleColumn();
                columns.add(roleColumn);
            }
        }
        return tables;
    }

    private static ColumnDefinition createRoleColumn() {
        ColumnDefinition roleColumn = new ColumnDefinition();
        roleColumn.setColumnName("role");
        ColDataType roleDataType = new ColDataType();
        roleDataType.setDataType("VARCHAR");
        roleDataType.setArgumentsStringList(Arrays.asList("100"));
        roleColumn.setColDataType(roleDataType);
        return roleColumn;
    }

    private static ColumnDefinition createAttributeColumn(Attribute attribute) {
        ColumnDefinition column = new ColumnDefinition();
        column.setColumnName(attribute.getName());
        // Set column type
        ColDataType colDataType = new ColDataType();
        colDataType.setDataType(DM2SQLTypeConversion.convert(attribute.getType()));
        colDataType.setArgumentsStringList(DM2SQLTypeConversion.addArgument(attribute.getType()));
        // Set column constraints
        column.setColDataType(colDataType);
        return column;
    }

    private static ColumnDefinition createIdentityColumn(Table table) {
        ColumnDefinition idColumn = new ColumnDefinition();
        idColumn.setColumnName(String.format("%1$s_id", table.getName()));
        ColDataType idDataType = new ColDataType();
        idDataType.setDataType("VARCHAR");
        idDataType.setArgumentsStringList(Arrays.asList("100"));
        idColumn.setColDataType(idDataType);
        idColumn.addColumnSpecs(Arrays.asList(MySQLConstraint.NOT_NULL, MySQLConstraint.PRIMARY_KEY));
        return idColumn;
    }

}
