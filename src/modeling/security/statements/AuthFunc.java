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

package modeling.security.statements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import modeling.api.SQLSIConfiguration;
import modeling.data.entities.Association;
import modeling.data.entities.Attribute;
import modeling.data.templates.SQLTemplate;
import modeling.security.utils.PrintingUtils;
import modeling.statements.CompoundStatement;

public class AuthFunc extends SQLAuthFunction {
	private List<AuthRoleFunc> functions;

	public List<AuthRoleFunc> getFunctions() {
		return functions;
	}

	public void setFunctions(List<AuthRoleFunc> functions) {
		this.functions = functions;
	}

	public AuthFunc(String action, String resource, List<AuthRoleFunc> functions) {
		super(action, resource);
		this.functions = functions;
	}

	@Override
	public String toString() {
		return String.format(SQLTemplate.AUTH_FUNC, getAuthFunBody(), this.getDelimiter());
	}

	public AuthFunc() {
		this.functions = new ArrayList<AuthRoleFunc>();
	}

	public AuthFunc(String action, String authFunName) {
		super(action, authFunName);
		this.functions = new ArrayList<AuthRoleFunc>();
	}

	@Override
	public String getName() {
		return String.format("auth_%s_%s", this.getAction(), this.getResource());
	}

	@Override
	public CompoundStatement getStatement() {
		CompoundStatement cs = new CompoundStatement();
		cs.setStatement(this.toString());
		return cs;
	}

	public String getAuthFunBody() {
		if (functions == null || functions.isEmpty()) {
			return "RETURN 0;\r\n";
		} else {
			String authRoleFuns = "RETURN 0;";
			for (int i = functions.size() - 1; i >= 0; i--) {
				AuthRoleFunc function = functions.get(i);
				authRoleFuns = String.format(SQLTemplate.AUTH_FUN_BODY, function.getRole(), function.getName(),
						function.getAuthFunParameters(), authRoleFuns);
			}
			return authRoleFuns;
		}
	}

	@Override
	public String getAuthFunParametersWithType() {
		return PrintingUtils.getParametersWithType(getParameters());
	}

	@Override
	public String getAuthFunParameters() {
		return PrintingUtils.getParameters(getParameters());
	}

	public void setParameters(Attribute attribute) {
		this.setParameters(new HashMap<String, String>());
		this.getParameters().put(SQLSIConfiguration.SELF, SQLSIConfiguration.PARAM_TYPE);
		this.getParameters().put(SQLSIConfiguration.CALLER, SQLSIConfiguration.PARAM_TYPE);
		this.getParameters().put(SQLSIConfiguration.ROLE, SQLSIConfiguration.PARAM_TYPE);
	}

	public void setParameters(Association association) {
		this.setParameters(new HashMap<String, String>());
		this.getParameters().put(String.format("%s%s", SQLSIConfiguration.PARAM_PREFIX, association.getLeftEnd().getOpp()),
				SQLSIConfiguration.PARAM_TYPE);
		this.getParameters().put(String.format("%s%s", SQLSIConfiguration.PARAM_PREFIX, association.getRightEnd().getOpp()),
				SQLSIConfiguration.PARAM_TYPE);
		this.getParameters().put(SQLSIConfiguration.CALLER, SQLSIConfiguration.PARAM_TYPE);
		this.getParameters().put(SQLSIConfiguration.ROLE, SQLSIConfiguration.PARAM_TYPE);
	}
}
