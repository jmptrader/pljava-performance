select * from perftesting.benchmark(20, ARRAY[
     'perftesting.pg_benchmark_select()',
     'perftesting.benchmark_select()'
 ]);