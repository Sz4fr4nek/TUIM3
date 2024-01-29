from typing import List

from fastapi import FastAPI, Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer
from sqlalchemy import create_engine, Column, Integer, String, Float, ForeignKey, REAL, DateTime, func
from sqlalchemy.orm import declarative_base, sessionmaker, relationship
import jwt
from datetime import datetime, timedelta
from pydantic import BaseModel
import uvicorn

app = FastAPI()

# Konfiguracja JWT
SECRET_KEY = "supersecretkey"
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 30


# Szyfrowanie i deszyfrowanie JWT tokena
def create_jwt_token(data: dict):
    expire = datetime.utcnow() + timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    data.update({"exp": expire})
    return jwt.encode(data, SECRET_KEY, algorithm=ALGORITHM)


def decode_jwt_token(token: str):
    payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
    return payload


# Konfiguracja bazy danych SQLite
DATABASE_URL = "sqlite:///identifier.sqlite"
Base = declarative_base()
engine = create_engine(DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)


class User(Base):
    __tablename__ = "users"
    user_id = Column(Integer, primary_key=True, index=True, autoincrement=True)
    username = Column(String, unique=True, nullable=False)
    password = Column(String, nullable=False)
    weight = Column(REAL, nullable=False)
    training_level = Column(String, nullable=False)
    jwt_token = Column(String)
    # Updated relationship
    training_history = relationship("TrainingHistory", back_populates="user")


class TrainingHistory(Base):
    __tablename__ = "trainingsHistory"
    trainingHistory_id = Column(Integer, primary_key=True, index=True, autoincrement=True)
    user_id = Column(Integer, ForeignKey('users.user_id'), nullable=False)
    training_name = Column(String, nullable=False)
    training_level = Column(String, nullable=False)
    date_saved = Column(DateTime, default=func.current_timestamp())
    # Updated relationship
    user = relationship("User", back_populates="training_history")


class Training(Base):
    __tablename__ = "trainings"
    training_id = Column(Integer, primary_key=True, index=True, autoincrement=True)
    training_name = Column(String, nullable=False)
    training_level = Column(String, nullable=False)
    exercises = relationship("Exercise", back_populates="training")


class Exercise(Base):
    __tablename__ = "exercises"
    exercise_id = Column(Integer, primary_key=True, index=True, autoincrement=True)
    training_id = Column(Integer, ForeignKey('trainings.training_id'), nullable=False)  # Dodaj ForeignKey
    exercise_name = Column(String, nullable=False)
    weight = Column(REAL, default=0.3)
    training = relationship("Training", back_populates="exercises")


# Tworzenie tabel w bazie danych
Base.metadata.create_all(bind=engine)


# Model Pydantic dla aktualizacji treningu
class TrainingUpdate(BaseModel):
    training_name: str
    training_level: str


# Model Pydantic dla aktualizacji ćwiczenia
class ExerciseUpdate(BaseModel):
    exercise_name: str
    weight: float


# Definicja modelu użytkownika w bazie danych

# Tworzenie tabeli w bazie danych
Base.metadata.create_all(bind=engine)


# Model danych dla rejestracji
class UserRegister(BaseModel):
    username: str
    password: str
    weight: float
    training_level: str


# Model danych dla logowania
class UserLogin(BaseModel):
    username: str
    password: str


# Ustawienia zabezpieczeń
oauth2_scheme = OAuth2PasswordBearer(tokenUrl="token")


@app.delete("/training_history/{training_history_id}")
async def delete_training_history(training_history_id: int):
    # Query for the record to delete
    db = SessionLocal()
    record_to_delete = db.query(TrainingHistory).filter(
        TrainingHistory.trainingHistory_id == training_history_id).first()

    if record_to_delete is None:
        raise HTTPException(status_code=404, detail=f"Training history with ID {training_history_id} not found")

    # Delete the record
    db.delete(record_to_delete)
    db.commit()

    return {"detail": "Training history deleted successfully"}


# Endpoint do rejestracji nowego użytkownika
@app.post("/register")
async def register_user(user_data: UserRegister):
    db = SessionLocal()
    user = db.query(User).filter(User.username == user_data.username).first()
    if user:
        db.close()
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Username already registered")

    new_user = User(username=user_data.username, password=user_data.password, weight=user_data.weight,
                    training_level=user_data.training_level)
    db.add(new_user)
    db.commit()
    db.close()

    return {"message": "User registered successfully"}


# Endpoint do logowania i uzyskiwania tokenu JWT
@app.post("/login")
async def login_for_access_token(user_data: UserLogin):
    db = SessionLocal()
    user = db.query(User).filter(User.username == user_data.username).first()

    if user and user_data.password == user.password:
        token_data = {"sub": user_data.username}
        token = create_jwt_token(token_data)

        # Aktualizacja tokenu JWT w bazie danych dla użytkownika
        user.jwt_token = token
        db.commit()

        # Zwróć accessToken, userName oraz id użytkownika
        response = {
            "accessToken": token,
            "userName": user.username,
            "userId": user.user_id,  # Zwracanie ID użytkownika
            "traning_level": user.training_level
        }

        # print(response)

        db.close()
        return response

    db.close()
    raise HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Invalid username or password",
        headers={"WWW-Authenticate": "Bearer"},
    )


class TrainingHistoryCreate(BaseModel):
    training_name: str
    training_level: str


@app.post("/add_training_history/{user_id}")
async def add_training_history(user_id: int, training_history: TrainingHistoryCreate):
    db = SessionLocal()
    new_training_history = TrainingHistory(
        user_id=user_id,
        training_name=training_history.training_name,
        training_level=training_history.training_level,
        date_saved=datetime.now()
    )
    db.add(new_training_history)
    db.commit()
    db.refresh(new_training_history)
    return new_training_history


class TrainingHistoryModel(BaseModel):
    training_name: str
    training_level: str
    date_saved: datetime
    trainingHistory_id: int


@app.get("/training_history/{user_id}", response_model=List[TrainingHistoryModel])
async def get_training_history(user_id: int):
    db = SessionLocal()
    training_history = db.query(TrainingHistory).filter(TrainingHistory.user_id == user_id).all()
    if training_history is None:
        raise HTTPException(status_code=404, detail="Training history not found")
    return training_history


@app.get("/trainings")
async def list_trainings(training_level: str):
    db = SessionLocal()
    # verify_token = decode_jwt_token(token)  # Add token verification logic
    trainings = db.query(Training).filter(Training.training_level == training_level).all()
    db.close()
    return trainings


@app.get("/training/{training_id}")
async def get_training(training_id: int):
    db = SessionLocal()
    # token: str = Depends(oauth2_scheme)
    # verify_token = decode_jwt_token(token)  # Add token verification logic
    training = db.query(Training).filter(Training.training_id == training_id).first()
    if training:
        exercises = db.query(Exercise).filter(Exercise.training_id == training_id).all()
        db.close()
        return {"training": training, "exercises": exercises}
    db.close()
    raise HTTPException(status_code=404, detail="Training not found")


@app.delete("/training/{training_id}")
async def delete_training(training_id: int):
    db = SessionLocal()
    # verify_token = decode_jwt_token(token)  # Add token verification logic
    training = db.query(Training).filter(Training.training_id == training_id).first()
    if training:
        db.delete(training)
        db.commit()
        db.close()
        return {"message": "Training deleted successfully"}
    db.close()
    raise HTTPException(status_code=404, detail="Training not found")

class UpdateProfileRequest(BaseModel):
    weight: float
    training_level: str

@app.put("/user/{user_id}")
async def update_profile(user_id: int, update_data: UpdateProfileRequest):
    db = SessionLocal()
    user = db.query(User).filter(User.user_id == user_id).first()
    if user:
        user.weight = update_data.weight
        user.training_level = update_data.training_level
        db.commit()
        db.close()
        return {"message": "Profile updated successfully"}
    db.close()
    raise HTTPException(status_code=404, detail="User not found")

@app.get("/user/{user_id}")
async def get_user(user_id: int):
    db = SessionLocal()
    user = db.query(User).filter(User.user_id == user_id).first()
    if user:
        return {
            "user_id": user.user_id,
            "weight": user.weight,
            "training_level": user.training_level
            # Add any other user fields you want to return
        }
    else:
        raise HTTPException(status_code=404, detail="User not found")

# Endpoint do aktualizacji treningu
@app.put("/training/{training_id}")
async def update_training(training_id: int, training_update: TrainingUpdate):
    db = SessionLocal()
    training = db.query(Training).filter(Training.training_id == training_id).first()
    if not training:
        db.close()
        raise HTTPException(status_code=404, detail="Training not found")

    training_data = training_update.dict(exclude_unset=True)
    for key, value in training_data.items():
        setattr(training, key, value)

    db.commit()
    db.close()
    return {"message": "Training updated successfully", "training": training}


# Endpoint do aktualizacji ćwiczenia
@app.put("/exercise/{exercise_id}")
async def update_exercise(exercise_id: int, exercise_update: ExerciseUpdate):
    db = SessionLocal()
    exercise = db.query(Exercise).filter(Exercise.exercise_id == exercise_id).first()
    if not exercise:
        db.close()
        raise HTTPException(status_code=404, detail="Exercise not found")

    exercise_data = exercise_update.dict(exclude_unset=True)
    for key, value in exercise_data.items():
        setattr(exercise, key, value)

    db.commit()
    db.close()
    return {"message": "Exercise updated successfully", "exercise": exercise}
