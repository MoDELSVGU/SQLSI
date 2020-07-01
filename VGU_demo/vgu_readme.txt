- data model uses generalizations.
- it does not work yet because OCL2PSQL does not support generalizations.

* SqlSI configuration
- main
JSONArray policy =  (JSONArray) new JSONParser().parse(new FileReader("/Users/clavel/VGU/Repositories/SqlSI/VGU_demo/vgu_policy.json"));
JSONArray context =  (JSONArray) new JSONParser().parse(new FileReader("/Users/clavel/VGU/Repositories/SqlSI/VGU_demo/vgu_context.json"));
JSONArray queries = (JSONArray) new JSONParser().parse(new FileReader("/Users/clavel/VGU/Repositories/SqlSI/VGU_demo/vgu_test.json"));

- SqlSIGenAuthFun
File file = new File("/Users/clavel/VGU/Repositories/SqlSI/VGU_demo/vgu_fun.sql");

- SqlSIGen
File file = new File("/Users/clavel/VGU/Repositories/SqlSI/VGU_demo/vgu_proc.sql");	