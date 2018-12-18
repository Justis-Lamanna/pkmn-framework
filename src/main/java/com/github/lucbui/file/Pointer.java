package com.github.lucbui.file;

public interface Pointer {
    long getLocation();

    static Pointer of(long position){
        return new Default(position);
    }

    class Default implements Pointer {

        long position;

        private Default(long position){
            this.position = position;
        }

        @Override
        public long getLocation() {
            return position;
        }
    }
}
