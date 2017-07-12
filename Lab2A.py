import simpy
from random import randint
from RWLock import RWLock

#creates reader object
def reader(env,transactionNumber, processTime,rwlock,readContainer,writeContainer):
    with rwlock.request() as req:
        yield req
        start = env.now
        #print('got request %d' %processTime)
        yield writeContainer.put(1)#checks to see if any writer is waiting
        yield writeContainer.get(1)
        yield readContainer.put(1)
        #print('put one in')
        yield env.timeout(processTime)
        yield readContainer.get(1)
        end = env.now
        
        print('finished reading transaction %d since request at %d' %(transactionNumber,end-start))
#creates writers object        
def writer(env,transactionNumber,processTime,rwlock,readContainer,writeContainer):
    with rwlock.request() as req:
        yield req
        start = env.now
        yield writeContainer.put(1)#shows writer is waiting
        #print('got write request')
        yield readContainer.put(numberOfDataBlocks)
        #print('filled container')
        yield env.timeout(processTime)
        yield writeContainer.get(1)
        yield readContainer.get(numberOfDataBlocks)
        end = env.now 
        print('dumped container')
        print('finished writing transaction %d with time%d'%(transactionNumber,end - start))
    
#creates transactions made up of reads and writes chosen randomly        
def rw_generator(env,transactionNumber,maxProcessTime,maxWaitTime,MaxNumberOfRequests, rwlock,readContainer,writeContainer):
    for i in range(0,MaxNumberOfRequests):
        choice = randint(1,5)
        if choice !=1:
            yield env.timeout(randint(0,maxWaitTime))
            env.process(reader(env,transactionNumber,randint(0,maxProcessTime),rwlock,readContainer,writeContainer))
        else:
            yield env.timeout(randint(0,maxWaitTime))
            env.process(writer(env,transactionNumber,randint(0,maxProcessTime),rwlock,readContainer,writeContainer))
    
        
        
def read_generator(env,maxProcessTime,maxWaitTime,MaxNumberOfRequests,rwlock,readContainer,writeContainer): #doesn't end up using this
    for i in range(0,MaxNumberOfRequests):
        yield env.timeout(randint(0,maxWaitTime))
        env.process(reader(env,randint(0,maxProcessTime),rwlock,readContainer,writeContainer))

def generate_random_processes(env,MaxWaitTime,MaxProcessTime,numberOfDataBlocks,MaxNumberOfRequests,rwlock,readContainer,writeContainer):
    for i in range(0,numberOfDataBlocks):
        #choice = randint(1,2)
        transactionNumber = i
        #if choice ==1:
        env.process(rw_generator(env,transactionNumber,MaxProcessTime,MaxWaitTime,MaxNumberOfRequests,rwlock,readContainer,writeContainer))
        #else:
            #env.process(read_generator(env,MaxProcessTime,MaxWaitTime*2,MaxNumberOfRequests,rwlock,readContainer,writeContainer))
    
MaxWaitTime = 10
MaxProcessTime = 5
numberOfDataBlocks = 100
MaxNumberOfRequests = 30
#rwlock = RWLock() #or this
env = simpy.Environment()
rwlock = simpy.Resource(env,numberOfDataBlocks)
readContainer  = simpy.Container(env,numberOfDataBlocks,init=0)
writeContainer  = simpy.Container(env,1,init=0)
generate_random_processes(env,MaxWaitTime,MaxProcessTime,numberOfDataBlocks,MaxNumberOfRequests,rwlock,readContainer,writeContainer)
env.run()

