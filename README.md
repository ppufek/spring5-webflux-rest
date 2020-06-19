[![CircleCI](https://circleci.com/gh/ppufek/spring5-webflux-rest.svg?style=svg)](https://circleci.com/gh/ppufek/spring5-webflux-rest)

# Spring Framework 5: Beginner to Guru Course
Generating **Reactive** RESTful API with Spring MVC & **WebFlux** (MongoDB project)

*Reactive Programming*  = useful implementation technique which focuses on non-blocking, asynchronous execution

*Reactive Programming* = asynchronous programming paradigm (pattern) focused on processing streams of data.

Data Streams = can be just about anything: 
- mouse clicks, or other user interactions
- JMS (Java Message Service) Messages, RESTful Service calls, Twitter feed, Stock Trades, list of data from a database
	
**STREAM** = sequence of events ordered in time (events you want to listen to)

*“Reactive programs also maintain a continuous interaction with their environment, but at a speed which is determined by the environment, not the program itself. Interactive programs work at their own pace and mostly deal with communication, while reactive programs only work in response to external demands and mostly deal with accurate interrupt handling. Real-time programs are usually reactive.”* – Gerad Berry, French Computer Scientist

Spring Reactive Types:

- “Mono” &rightarrow; Publisher with 0 or 1 elements in data stream
- “Flux” &rightarrow; Publisher with 0 or MANY elements in the data stream

&rightarrow;	Both types implement Reactive Streams Publisher interface