import ACCESS_ENUM from "@/access/accessEnum";

const checkAccess = (
  loginUser: API.LoginUserVO,
  needAccess = ACCESS_ENUM.NOT_LOGIN
) => {
  const loginUserAccess = loginUser.userRole ?? ACCESS_ENUM.NOT_LOGIN;
  if (needAccess === ACCESS_ENUM.NOT_LOGIN) {
    //不需要登录
    return true;
  }
  if (needAccess === ACCESS_ENUM.USER) {
    if (loginUserAccess === ACCESS_ENUM.NOT_LOGIN) {
      return false;
    }
  }
  if (needAccess === ACCESS_ENUM.ADMIN) {
    if (
      loginUserAccess === ACCESS_ENUM.NOT_LOGIN ||
      loginUserAccess === ACCESS_ENUM.USER
    ) {
      return false;
    }
  }
  return true;
};
export default checkAccess;
