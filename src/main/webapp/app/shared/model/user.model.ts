export interface IUser {
  id?: any;
  login?: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  activated?: boolean;
  langKey?: string;
  authorities?: any[];
  createdBy?: string;
  createdDate?: Date | null;
  lastModifiedBy?: string;
  lastModifiedDate?: Date | null;
  password?: string;
}

export const defaultValue: Readonly<IUser> = {
  id: undefined,
  login: '',
  firstName: '',
  lastName: '',
  email: '',
  activated: true,
  langKey: 'es',
  authorities: [],
  createdBy: '',
  createdDate: null,
  lastModifiedBy: '',
  lastModifiedDate: null,
  password: '',
};
