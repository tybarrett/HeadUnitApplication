package com.example.headunitapplication;

public abstract class CallbackObject<T> {

    public void update(Object obj) {
        if (obj == null) {
            System.err.println("The object passed into " + this.getClass().getSimpleName() + " is null.");
        }

        T typedObj = null;

        try {
            typedObj = (T) obj;
        } catch (ClassCastException e) {
            System.err.println("The object passed into " + this.getClass().getSimpleName() + " is " +
                    "of type " + obj.getClass().getSimpleName() + " which was unexpected.");
        }

        if (typedObj != null) {
            safe_update(typedObj);
        }

    }

    public void safe_update(T obj) {}

}
