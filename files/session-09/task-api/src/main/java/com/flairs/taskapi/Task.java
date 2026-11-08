package com.flairs.taskapi;

// A record is perfect for data. Jackson turns it into JSON and back with no extra code.
// {"id":1,"title":"Learn Spring","done":false}
//
// `done` is Boolean (not boolean) on purpose. Boot 4 uses Jackson 3, which rejects a missing field
// for a primitive. With Boolean, a client may leave "done" out, and the compact constructor makes it false.
public record Task(Long id, String title, Boolean done) {

    public Task {
        if (done == null) {
            done = false;
        }
    }
}
