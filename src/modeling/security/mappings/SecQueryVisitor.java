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

package modeling.security.mappings;

import java.util.List;
import java.util.Stack;

import org.json.simple.JSONArray;

import modeling.data.entities.DataModel;
import modeling.security.statements.AuthFunc;
import modeling.statements.SQLTemporaryTable;
import net.sf.jsqlparser.statement.Block;
import net.sf.jsqlparser.statement.Commit;
import net.sf.jsqlparser.statement.CreateFunctionalStatement;
import net.sf.jsqlparser.statement.DeclareStatement;
import net.sf.jsqlparser.statement.DescribeStatement;
import net.sf.jsqlparser.statement.ExplainStatement;
import net.sf.jsqlparser.statement.IfElseStatement;
import net.sf.jsqlparser.statement.PurgeStatement;
import net.sf.jsqlparser.statement.ResetStatement;
import net.sf.jsqlparser.statement.RollbackStatement;
import net.sf.jsqlparser.statement.SavepointStatement;
import net.sf.jsqlparser.statement.SetStatement;
import net.sf.jsqlparser.statement.ShowColumnsStatement;
import net.sf.jsqlparser.statement.ShowStatement;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.UnsupportedStatement;
import net.sf.jsqlparser.statement.UseStatement;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.alter.AlterSession;
import net.sf.jsqlparser.statement.alter.AlterSystemStatement;
import net.sf.jsqlparser.statement.alter.RenameTableStatement;
import net.sf.jsqlparser.statement.alter.sequence.AlterSequence;
import net.sf.jsqlparser.statement.analyze.Analyze;
import net.sf.jsqlparser.statement.comment.Comment;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.schema.CreateSchema;
import net.sf.jsqlparser.statement.create.sequence.CreateSequence;
import net.sf.jsqlparser.statement.create.synonym.CreateSynonym;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.view.AlterView;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.execute.Execute;
import net.sf.jsqlparser.statement.grant.Grant;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.merge.Merge;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.show.ShowIndexStatement;
import net.sf.jsqlparser.statement.show.ShowTablesStatement;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.upsert.Upsert;
import net.sf.jsqlparser.statement.values.ValuesStatement;

public class SecQueryVisitor implements StatementVisitor {
    private DataModel dataModel;
    public DataModel getDataModel() {
        return dataModel;
    }

    public void setDataModel(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    private List<AuthFunc> functions;
    private JSONArray parameters;
    private Stack<SQLTemporaryTable> result = new Stack<SQLTemporaryTable>();

    public JSONArray getParameters() {
        return parameters;
    }

    public void setParameters(JSONArray parameters) {
        this.parameters = parameters;
    }

    public List<AuthFunc> getFunctions() {
        return functions;
    }

    public void setFunctions(List<AuthFunc> functions) {
        this.functions = functions;
    }

    public Stack<SQLTemporaryTable> getResult() {
        return result;
    }

    public void setResult(Stack<SQLTemporaryTable> result) {
        this.result = result;
    }

    @Override
    public void visit(Commit commit) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Delete delete) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Update update) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Insert insert) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Drop drop) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Truncate truncate) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(CreateIndex createIndex) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(CreateTable createTable) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(CreateView createView) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(AlterView alterView) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Alter alter) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Statements stmts) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Execute execute) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(SetStatement set) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Merge merge) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Select select) {
        SelectInjector injector = new SelectInjector();
        injector.setAction("READ");
        injector.setFunctions(functions);
        injector.setParameters(this.getParameters());
        injector.setDataModel(dataModel);
        SelectBody selectBody = select.getSelectBody();
        selectBody.accept(injector);
        this.getResult().addAll(injector.getResults());
    }

    @Override
    public void visit(Upsert upsert) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(UseStatement use) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Comment arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ShowColumnsStatement arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Block arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ValuesStatement arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(DescribeStatement arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ExplainStatement arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ShowStatement arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(DeclareStatement aThis) {
        // TODO Auto-generated method stub
        
    }

	@Override
	public void visit(Analyze analyze) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(SavepointStatement savepointStatement) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(RollbackStatement rollbackStatement) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(CreateSchema aThis) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ResetStatement reset) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ShowIndexStatement showIndex) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ShowTablesStatement showTables) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Grant grant) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(CreateSequence createSequence) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(AlterSequence alterSequence) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(CreateFunctionalStatement createFunctionalStatement) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(CreateSynonym createSynonym) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(AlterSession alterSession) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(IfElseStatement aThis) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(RenameTableStatement renameTableStatement) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(PurgeStatement purgeStatement) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(AlterSystemStatement alterSystemStatement) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(UnsupportedStatement unsupportedStatement) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Replace replace) {
		// TODO Auto-generated method stub
		
	}

}
