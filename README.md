# Wait-Group
A Java implementation of Golang's [WaitGroup](https://pkg.go.dev/sync#WaitGroup) interface

![image](resources/hourglass.jpg)

## What does this implementation covers?
 - Java implementation for all the methods exposed by `WaitGroup` interface
 - `WaitGroup` is reusable as per the interface contract
 - Panic exception in case `WaitGroup` counter reaches a negative value

## How to use it?
The usage is similar to Golang's interface. The flow looks as below:

#### Create a `WaitGroup` instance
```agsl
WaitGroup wg = new WaitGroupImpl();
```
#### Add a delta for number of processes that are running in the background
```
wg.add(delta);
```
#### Each background process marks `WaitGroup` as `done` once it finishes processing
```agsl
void run() {
    // some processing
    wg.done();
}
```

#### Main thread awaits for all the processes to finish
````agsl
wg.wgWait();
````