# README #

This page contains a few examples of CS projects I've worked on. 

## DATABASE MANAGEMENT SYSTEMS - LAB 2 ##
### TRANSACTIONS AND CONCURRENCY CONTROL ###
Part A: With the typical strategy, where there are multiple readers and a single writer, there is a chance of starvation. Please suggest and implement a solution that would avoid starvation.

* Read requests: if there is no lock on the data, then grant read lock for the period of time requested. If there is a write lock, wait until the 
read lock is free and it's the reader's turn.

* Write requests: if there is no write lock on the data, then grant write lock for the period of time requested. If there is a write lock, wait until the write lock is free and it's the writer's turn.

Part B: Invalid Dirty Writes - this approach assumes that the likelihood of write conflict is rare. Thus, locks to read or write are not acquired, but rather the timestamp of the last read/write is used. If the timestamp of the last read/write is after the start of the current write, the current write is invalidated and the transaction is reattempted.