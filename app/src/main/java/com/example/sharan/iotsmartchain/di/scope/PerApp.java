package com.example.sharan.iotsmartchain.di.scope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by Sharan on 20-03-2018.
 */

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface PerApp {
}
