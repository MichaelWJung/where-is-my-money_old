# Where is My Money

An Android app for personal finance tracking using a double-entry bookkeeping system. It is inspired heavily by [Gnucash](https://www.gnucash.org/). Development is in the early stages.

## Technology

The business logic is implemented in ClojureScript and is supposed to be shared with a future desktop version of the application.
On Android it is run in a Microservice running on Node.js (using [LiquidCore](https://github.com/LiquidPlayer/LiquidCore)).
The frontend is a native Java Android frontend. 
_Where is My Money_ makes use of [re-frame](https://github.com/day8/re-frame) to manage applacation state and drive UI changes.
