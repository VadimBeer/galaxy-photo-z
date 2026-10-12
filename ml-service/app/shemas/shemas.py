from pydantic import BaseModel, ConfigDict
from pydantic.alias_generators import to_camel

# прием сообщения
class ImgTaskMessage(BaseModel):
    model_config = ConfigDict(alias_generator=to_camel, populate_by_name=True)

    task_id: int
    user_id: int
    model_version: int
    object_key: str

    def get_object_key(self) -> str:
        return self.object_key