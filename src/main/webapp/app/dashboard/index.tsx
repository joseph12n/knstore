import React from 'react';

const Admin = React.lazy(() => import(/* webpackChunkName: "administration" */ 'app/modules/administration'));

export { default as EntitiesRoutes } from 'app/entities/routes';
export { Admin };
