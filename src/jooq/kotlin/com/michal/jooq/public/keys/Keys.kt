@file:Suppress("warnings")
/*
 * This file is generated by jOOQ.
 */
package com.michal.jooq.`public`.keys


import org.jooq.UniqueKey
import org.jooq.impl.DSL
import org.jooq.impl.Internal



// -------------------------------------------------------------------------
// UNIQUE and PRIMARY KEY definitions
// -------------------------------------------------------------------------

val EVENT_STORE_PKEY: UniqueKey<com.michal.jooq.`public`.tables.records.EventStoreRecord> = Internal.createUniqueKey(com.michal.jooq.`public`.tables.EventStore.EVENT_STORE, DSL.name("event_store_pkey"), arrayOf(com.michal.jooq.`public`.tables.EventStore.EVENT_STORE.ID), true)
val UNIQUE_STREAM_ID_VERSION: UniqueKey<com.michal.jooq.`public`.tables.records.EventStoreRecord> = Internal.createUniqueKey(com.michal.jooq.`public`.tables.EventStore.EVENT_STORE, DSL.name("unique_stream_id_version"), arrayOf(com.michal.jooq.`public`.tables.EventStore.EVENT_STORE.STREAM_ID, com.michal.jooq.`public`.tables.EventStore.EVENT_STORE.VERSION), true)
