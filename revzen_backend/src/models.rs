//! Models used to extract from and insert into the database.
use crate::schema::{follows, histories, pets, users};
use std::time::SystemTime;

/// User struct representing a record in the users table
#[derive(Identifiable, Queryable)]
pub struct User {
    pub friendcode: i32,
    pub id: i64,
    pub username: String,
    pub main_pet: i32,
}

/// Data required to insert the user (friendcode is a serial - automatically
/// generated number incremented for each new record)
#[derive(Insertable)]
#[table_name = "users"]
pub struct AddUser {
    pub id: i64,
    pub username: String,
    pub main_pet: i32,
}

/// Data required to add a user session to the histories table
#[derive(Insertable)]
#[table_name = "histories"]
pub struct Session {
    pub sub: i64,
    pub session_time: SystemTime,
    pub plan_study_time: i32,
    pub plan_break_time: i32,
    pub study_time: i32,
    pub break_time: i32,
}

#[derive(Identifiable, Queryable)]
#[table_name = "histories"]
pub struct History {
    pub id: i32,
    pub sub: i64,
    pub session_time: SystemTime,
    pub plan_study_time: i32,
    pub plan_break_time: i32,
    pub study_time: i32,
    pub break_time: i32,
}

/// Data required to insert a new friend request into the friends table
#[derive(Insertable, Identifiable, Queryable)]
#[primary_key(followee, follower)]
pub struct Follow {
    pub followee: i64,
    pub follower: i64,
    pub accepted: bool,
}

#[derive(Insertable, Identifiable, Queryable, AsChangeset)]
#[primary_key(user_id, pet_type)]
pub struct Pet {
    pub user_id: i64,
    pub pet_type: i32,
    pub health: i32,
    pub xp: i32,
}
