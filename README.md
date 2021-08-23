Reseting Wiremock mappings:
```shell
curl -XPOST http://localhost:9999/__admin/reset
```

Inserting **10_000** random proposals into table on PostgreSQL:
```shell
insert into proposal (
	id, address, created_at, "document", email, "name", salary, status, updated_at
)
select
	 md5(random()::text || clock_timestamp()::text)::uuid as id
	,'Rua das Tabajaras, ' || floor(random() * 9999)::int as address
	,localtimestamp                                       as created_at
	,left(md5(random()::text), 16)                        as "document"
	,left(md5(random()::text), 6) || '@zup.com.br'        as email
	,'Customer ' || left(md5(random()::text), 22)         as "name"
	,trunc((random() * 99999)::numeric, 2)                as salary
	,'ELIGIBLE'                                           as status
	,localtimestamp                                       as updated_at
  from generate_series(1, 10000) s(i)
```