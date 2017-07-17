DATABASE TRANSACTION AND CONCURRENCY CONTROL SIMULATION

Part A: With the typical strategy, where there are multiple readers and a single writer, there is a chance of starvation. Please suggest and implement a solution that would avoid starvation.

* Read requests: if there is no lock on the data, then grant read lock for the period of time requested. If there is a write lock, wait until the 
read lock is free and it's the reader's turn.

* Write requests: if there is no write lock on the data, then grant write lock for the period of time requested. If there is a write lock, wait until the write lock is free and it's the writer's turn.

Part B: Invalid Dirty Writes - this approach assumes that the likelihood of write conflict is rare. Thus, locks to read or write are not acquired, but rather the timestamp of the last read/write is used. If the timestamp of the last read/write is after the start of the current write, the current write is invalidated and the transaction is reattempted.

Results:
Changing the processing time for each read/write transaction has a large effect on the amount of dirty writes in the simulation.  When I multiplied the initial read/write generating random procTime by a factor of 0.5, and the new procTime (after an invalid write) by a factor of 0.25, the percentage of dirty write transactions goes down from around 0.047 (in each of the 10 runs) to 0.024 (see RUN 11).  When I reduce the processing time more drastically, by factors of 0.05 and 0.025 respectively, the percentage of invalid write transaction drops further to 0.00227. (RUN 12)
Another parameter which has a large effect on the amount of dirty writes is the time between each process being generated.  When I yield env.timeout(randint(0,5)), instead of the env.timeout(randint(0,10)) which gave < 0.05, the simulation had an invalid write percentage of 0.095.  This makes sense- if we're generating writes more often, there's a higher likelihood of conflict.  Changing the number of trials from 10,000 to 100,000 to 1,000,000 did not seem to have significant effects on the percentage of conflicting transactions in the simulation.  When I set the number of trials as low as 1000, however, the percentage of dirty writes was consistently around 0.035.  This demonstrates that it is important to have enough samples in your dataset so that you don't get skewed results. 
