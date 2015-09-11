

http://developer.android.com/reference/android/util/Log.html


## When inside a catch clause

We can use ```Log.x(APP_TAG, e.getMessage(), e)```

Sending the exception object at the end will (hopefully) not lose the exception object

The message can be replaced by a custom message


## When logging

```Log.x(APP_TAG, e.getMessage())```

The message can be replaced by a custom message

