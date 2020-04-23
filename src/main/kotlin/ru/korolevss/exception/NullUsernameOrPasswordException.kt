package ru.korolevss.exception

import java.lang.RuntimeException

class NullUsernameOrPasswordException(message: String): RuntimeException(message)