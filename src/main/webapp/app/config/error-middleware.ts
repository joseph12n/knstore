const getErrorMessage = errorData => {
  let { message } = errorData;
  if (errorData.fieldErrors) {
    errorData.fieldErrors.forEach(fErr => {
      message += `\nfield: ${fErr.field}, Object: ${fErr.objectName}, message: ${fErr.message}\n`;
    });
  }
  return message;
};

export default () => next => action => {
  /**
   *
   * The error middleware serves to log error messages from dispatch
   * It need not run in production
   */
  if (DEVELOPMENT) {
    const { error } = action;
    if (error) {
      const status = error.response?.status;
      const url = error.config?.url ?? '';
      const isExpectedAuthError = status === 401 && (url.endsWith('api/account') || url.endsWith('api/authenticate'));
      if (!isExpectedAuthError) {
        console.error(`${action.type} caught at middleware with reason: ${JSON.stringify(error.message)}.`);
        if (error.response?.data) {
          const message = getErrorMessage(error.response.data);
          console.error(`Actual cause: ${message}`);
        }
      }
    }
  }
  // Dispatch initial action
  return next(action);
};
