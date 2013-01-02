select * from perftesting.benchmark(5, ARRAY[
     'perftesting.pg_benchmark_select()',
     'perftesting.benchmark_select()'
 ]);