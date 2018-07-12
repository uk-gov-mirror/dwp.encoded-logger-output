<a name="2.0.0"></a>
# 2.0.0 (2018-07-12)


### Bug Fixes

* remove pound sign from test as it doesn't exist in UTF-8 charset according to docs ([72f9292](https://gitlab.itsshared.net/SecureComms/encoded-logger-output/commit/72f9292))
* **default:** fix the default log level to be INFO for both logback and log4j configuration xml files ([6d2274e](https://gitlab.itsshared.net/SecureComms/encoded-logger-output/commit/6d2274e))


### Features

* first commit of json output. Incomplete. ([b0523d5](https://gitlab.itsshared.net/SecureComms/encoded-logger-output/commit/b0523d5))
* pass in app name and version/nChange to pass in the app name and version, leaving the logic of how to get that info upto the application using the class/nATW388 ([e20dd31](https://gitlab.itsshared.net/SecureComms/encoded-logger-output/commit/e20dd31))
* **log:** all methods of getLogger call the same implementation and logging of app details will default to <not-specified> if not passes (was previously logged as ${ctx.app_name} and ${ctx.app_version} in the log file ([6fbe94b](https://gitlab.itsshared.net/SecureComms/encoded-logger-output/commit/6fbe94b))



