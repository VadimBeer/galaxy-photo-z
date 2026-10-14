from __future__ import annotations

from enum import Enum

from pydantic import BaseModel, ConfigDict
from pydantic.alias_generators import to_camel

class Status(str, Enum):
    PENDING = "PENDING"
    PROCESSING = "PROCESSING"
    COMPLETED = "COMPLETED"
    FAILED = "FAILED"

class QuantilePoint(BaseModel):
    tau: float
    value: float

# прием сообщения
class ImgTaskMessage(BaseModel):
    model_config = ConfigDict(alias_generator=to_camel, populate_by_name=True)

    task_id: int
    user_id: int
    model_version_id: int
    object_key: str

    def get_object_key(self) -> str:
        return self.object_key


class ChartTaskPublisher(BaseModel):
    model_config = ConfigDict(alias_generator=to_camel, populate_by_name=True)

    task_id: int
    user_id: int
    chart_object_key: str | None = None
    quantiles: list[QuantilePoint] | None = None
    median_quantile: float | None
    state: Status
    error_message: str | None = None
    content_type: str | None = None