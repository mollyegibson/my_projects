#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Wed Nov 30 19:41:56 2016

@author: mgibson
"""

 #!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Sun Nov 27 19:09:30 2016

@author: mgibson
"""
import time
import simpy
import simpy.rt
#from simpy.events import AllOf, Event
from random import randint
#import collections

env = simpy.rt.RealtimeEnvironment(initial_time=0, factor=0.00000000001, strict=False)

init = time.perf_counter()   # timestamp of t=0 (when we begin the simulation)

numDataBlocks = 2048
datablocks = [init] * numDataBlocks #give each datablock an initial value of the timestamp at which we start

#invstarts = []
numTrials = 1000

dirtywrites = [0] * numDataBlocks  #array to keep track of each block's number of dirty writes

totalwrites = [0] * numDataBlocks  #array to keep track of total number of write transactions on each block

def reader(env, procTime, transNum):
    #no constraints on a new read
    
    yield env.timeout(procTime) #   , value = end) #the read event, which takes a random amount of processing time

    #print(str(rt - init))
#==============================================================================
#   start = time.perf_counter() 
    #print('read start: %.2f' % start)
#==============================================================================

    
def writer(env, procTime, transNum, block):  #pass it a random processing time, a transaction number (to keep track of how many have happened) and the (randomly chosen) index of a data block
    start = time.perf_counter() #take note of what time the request is made
    print('Starting write on datablock %i at %.2f' % (block, start)) 
    totalwrites[block] += 1  
    #start = start #- init
    #w = yield env.timeout(procTime, value=start)
    yield env.timeout(procTime)
    while start <= datablocks[block]:   #if the request is made before a current write is finished
        #update
        rightnow = time.perf_counter()
        print('invalid write on datablock %i! starting over at %.2f' % (block, rightnow))
        dirtywrites[block] += 1
        npt = (randint(10, 1000))  #wait a random amount of (new processing) time
        yield env.timeout(npt)
        start = time.perf_counter()      #try to start the write again- will exit the while loop once the current write has finished
    
    end = time.perf_counter()
    datablocks[block] = end #add current write's timestamp to the datablock
    print('Finished write on datablock %i at %.2f' % (block, end))

    
def genRWs(env):  #generate the read/write processes
    #numWrites = 0
    for i in range(0, numTrials):
        rand = randint(1,2)  #randomly choose whether to generate a read or a write
        proctime = (randint(10, 1000)) #and randomly generate a process time for that read/write
        blocknum = randint(0, numDataBlocks - 1)  #and randomly choose which datablock to perform the action on (chooses an index of datablocks array)
        if rand != 1:
            #yield env.timeout(randint(0,15))
            #currentRWs.append(env.process(reader(env, i)))
            env.process(reader(env,proctime, i))
            #currentRWs.append(a)
        else:
            yield env.timeout(randint(0,10))
            #numWrites += 1
            env.process(writer(env, proctime,i, blocknum))  #this won't work bc they haven't been processed yet

g = env.process(genRWs(env))      

env.run()


print(sum(dirtywrites)/float(sum(totalwrites)))  #calculate the percentage of dirty write events

